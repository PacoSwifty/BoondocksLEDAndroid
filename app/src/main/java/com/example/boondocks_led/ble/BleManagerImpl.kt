package com.example.boondocks_led.ble

import android.annotation.SuppressLint
import android.bluetooth.*
import android.bluetooth.le.*
import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.UUID
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "BleManager"

private val BOON_SERVICE_UUID = UUID.fromString("b00d0c55-1111-2222-3333-0000b00d0c50")
private val CHARACTERISTIC_LED_SET = "b00d0c55-1111-2222-3333-0000b00d0c52"
const val CHARACTERISTIC_BRIGHT_SET = "b00d0c55-1111-2222-3333-0000b00d0c53"
const val CHARACTERISTIC_ALL_OFF = "b00d0c55-1111-2222-3333-0000b00d0c54"
const val CHARACTERISTIC_SCENE_SELECT = "b00d0c55-1111-2222-3333-0000b00d0c55"
const val CHARACTERISTIC_SCENE_SAVE = "b00d0c55-1111-2222-3333-0000b00d0c56"
const val CHARACTERISTIC_CTRL_TYPE_SET = "b00d0c55-1111-2222-3333-0000b00d0c57"
const val CHARACTERISTIC_READ_CONFIG = "b00d0c55-1111-2222-3333-0000b00d0c51"

enum class BoonLEDCharacteristic(val uuid: UUID) {
    LedSet(UUID.fromString(CHARACTERISTIC_LED_SET)),
    BrightSet(UUID.fromString(CHARACTERISTIC_BRIGHT_SET)),
    AllOff(UUID.fromString(CHARACTERISTIC_ALL_OFF)),
    SceneSelect(UUID.fromString(CHARACTERISTIC_SCENE_SELECT)),
    SceneSave(UUID.fromString(CHARACTERISTIC_SCENE_SAVE)),
    CtrlTypeSet(UUID.fromString(CHARACTERISTIC_CTRL_TYPE_SET)),
}

data class WriteRequest(
    val target: BoonLEDCharacteristic,
    val payload: ByteArray,
    val writeType: Int = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT,
    val controllerId: String? = null
)

@Singleton
class BleManagerImpl @Inject constructor(
    @ApplicationContext private val appContext: Context
) : BleManager {

    // ---------- Public flows ----------
    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Idle)
    override val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    private val _incoming = MutableSharedFlow<ByteArray>(
        replay = 0,
        extraBufferCapacity = 64
    )
    override val incoming: SharedFlow<ByteArray> = _incoming.asSharedFlow()

    @Volatile
    private var inflightRequest: WriteRequest? = null
    private val configuredControllers = MutableStateFlow<Set<String>>(emptySet())
    private val pendingCtrlType = mutableMapOf<String, ByteArray>()
    private val pendingMutex = Mutex()


    // ---------- Android BLE objects ----------
    private val bluetoothManager: BluetoothManager by lazy {
        appContext.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    }

    private val adapter: BluetoothAdapter? get() = bluetoothManager.adapter
    private val scanner: BluetoothLeScanner? get() = adapter?.bluetoothLeScanner

    @Volatile
    private var gatt: BluetoothGatt? = null

    //    @Volatile private var writeCharacteristic: BluetoothGattCharacteristic? = null
    @Volatile
    private var characteristicMap: Map<BoonLEDCharacteristic, BluetoothGattCharacteristic> =
        emptyMap()

    @Volatile
    private var connectedDevice: BluetoothDevice? = null


    // ---------- Coroutines / lifecycle ----------
    private val job = SupervisorJob()
    private val scope = CoroutineScope(job + Dispatchers.IO)

    private val started = AtomicBoolean(false)
    private val stopRequested = AtomicBoolean(false)

    // Write queue: serialize writes and wait for onCharacteristicWrite
    private val writeQueue = Channel<WriteRequest>(capacity = Channel.BUFFERED)

    private val writeAck = Channel<Pair<WriteRequest, Boolean>>(capacity = Channel.RENDEZVOUS)

    // ---------- Config ----------
    private val targetName = "BoonLED"
    private val desiredMtu = 512

    // Exponential backoff for reconnect
    private suspend fun reconnectDelay(attempt: Int) {
        // 0 -> 250ms, 1 -> 500ms, 2 -> 1s, ... cap ~10s
        val ms = (250L * (1L shl attempt.coerceAtMost(5))).coerceAtMost(10_000L)
        delay(ms)
    }


    // ---------- Public API ----------
    override suspend fun start() {
        if (!started.compareAndSet(false, true)) return
        stopRequested.set(false)

        scope.launch { writeLoop() }
        scope.launch { connectLoop() }
    }

    override suspend fun stop() {
        stopRequested.set(true)
        started.set(false)

        // Stop scan + disconnect
        withContext(Dispatchers.IO) {
            stopScanInternal()
            disconnectInternal("stop() called")
        }

        job.cancelChildren()
        _connectionState.value = ConnectionState.Idle
    }


    override suspend fun send(characteristic: BoonLEDCharacteristic, bytes: ByteArray) {
        ensureReady()
        Log.i(TAG, "Sending BLE Message: ${bytes.decodeToString()}")

        writeQueue.send(WriteRequest(target = characteristic, payload = bytes))
    }

    override fun trySend(characteristic: BoonLEDCharacteristic, bytes: ByteArray): Boolean {
        if (stopRequested.get()) return false
        Log.i(TAG, "trySend: ${characteristic} with ${bytes.decodeToString()}")

        scope.launch {
            runCatching { send(characteristic, bytes) }
                .onFailure { Log.w(TAG, "trySend failed: ${it.message}", it) }
        }
        return true
    }

    override suspend fun sendForController(
        controllerId: String,
        characteristic: BoonLEDCharacteristic,
        bytes: ByteArray
    ) {
        ensureReady()
        ensureConfigured(controllerId)

        writeQueue.send(
            WriteRequest(
                target = characteristic,
                payload = bytes,
                controllerId = controllerId
            )
        )
    }

    override fun trySendForController(
        controllerId: String,
        characteristic: BoonLEDCharacteristic,
        bytes: ByteArray
    ): Boolean {
        if (stopRequested.get()) return false

        scope.launch {
            runCatching { sendForController(controllerId, characteristic, bytes) }
                .onFailure { Log.w(TAG, "trySendForController failed: ${it.message}") }
        }
        return true
    }

    // ---------- Core loops ----------
    @SuppressLint("MissingPermission")

    private suspend fun connectLoop() {
        var attempt = 0
        while (!stopRequested.get()) {
            try {
                // If we already have a connected & ready gatt, just idle.
                if (isReady()) {
                    delay(500)
                    attempt = 0
                    continue
                }

                _connectionState.value = ConnectionState.Scanning
                val device = scanForTargetDevice(timeoutMs = 10_000)
                if (device == null) {
                    _connectionState.value =
                        ConnectionState.Disconnected("scan timeout / not found")
                    reconnectDelay(attempt++)
                    continue
                }

                _connectionState.value = ConnectionState.Connecting
                connectGattInternal(device)

                // Wait until services discovered + write characteristic cached
                ensureReady()
                attempt = 0
                _connectionState.value = ConnectionState.Connected(device.name, device.address)

                // Stay here until disconnected
                while (!stopRequested.get() && isReady()) {
                    delay(500)
                }
            } catch (t: Throwable) {
                _connectionState.value = ConnectionState.Error("connectLoop error", t)
                reconnectDelay(attempt++)
            }
        }
    }

    private suspend fun writeLoop() {
        for (payload in writeQueue) {
            if (stopRequested.get()) return

            inflightRequest = payload
            Log.i(TAG, "WriteLoop: Just got inflight request: ${payload.target} with ${payload.payload.decodeToString()}")
            try {
                ensureReady()
                val g = gatt ?: continue
                val c = characteristicMap[payload.target]
                if (c == null) {
                    Log.w(TAG, "Characteristic ${payload.target} not available; dropping write")
                    continue
                }

                val ok = writeCharacteristicInternal(g, c, payload.payload, payload.writeType)
                if (!ok) {
                    Log.w(TAG, "writeCharacteristic returned false; forcing disconnect")
                    disconnectInternal("writeCharacteristic returned false")
                    continue
                }

                val ackOk = withTimeoutOrNull(5_000) {
                    val (ackedReq, success) = writeAck.receive()
                    if (ackedReq !== payload) Log.w(TAG, "Ack did not match request instance")
                    success
                } ?: false

                if (!ackOk) {
                    Log.w(TAG, "write ack timeout/failure; forcing disconnect")
                    disconnectInternal("write ack timeout/failure")
                } else {
                    onWriteSucceeded(payload)
                }
            } catch (t: Throwable) {
                Log.e(TAG, "writeLoop error", t)
                disconnectInternal("writeLoop exception: ${t.message}")
            } finally {
                inflightRequest = null
            }
        }
    }

    // ---------- Scanning ----------
    @SuppressLint("MissingPermission")
    private suspend fun scanForTargetDevice(timeoutMs: Long): BluetoothDevice? {
        val s = scanner ?: run {
            _connectionState.value = ConnectionState.Error("No BLE scanner available")
            return null
        }

        val result = CompletableDeferred<BluetoothDevice?>()

        val callback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, scanResult: ScanResult?) {
                val device = scanResult?.device ?: return
                val name = device.name ?: return
                if (name == targetName && !result.isCompleted) {
                    result.complete(device)
                }
            }

            override fun onScanFailed(errorCode: Int) {
                if (!result.isCompleted) {
                    result.completeExceptionally(IllegalStateException("Scan failed: $errorCode"))
                }
            }
        }

        // Optional: filter by service UUID can reduce noise (if your device advertises it)
        val filters = listOf(
            ScanFilter.Builder()
                .setDeviceName(targetName)
                .build()
        )
        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()

        try {
            s.startScan(filters, settings, callback)
            return withTimeoutOrNull(timeoutMs) { result.await() }
        } finally {
            try {
                s.stopScan(callback)
            } catch (_: Throwable) { /* ignore */
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun stopScanInternal() {
        // We use per-scan callbacks so nothing to stop here globally
        // (Left intentionally blank.)
    }

    // ---------- GATT connect/disconnect ----------
    @SuppressLint("MissingPermission")
    private fun connectGattInternal(device: BluetoothDevice) {
        disconnectInternal("connectGattInternal: closing prior gatt")
        connectedDevice = device

        // Reset readiness
        if (ready.isCompleted) {
            // ready is a CompletableDeferred<Unit>; recreate if already completed
            // easiest: replace with a new instance (hack: reflection not needed; we keep it mutable via field)
        }
        // Instead of replacing field, we use a helper:
        resetReady()

        val callback = gattCallback
        gatt =
            device.connectGatt(appContext, false, callback, BluetoothDevice.TRANSPORT_LE)
    }

    @SuppressLint("MissingPermission")

    private fun disconnectInternal(reason: String) {
        try {

            characteristicMap = emptyMap()
            configuredControllers.value = emptySet()
            connectedDevice = null
            if (!ready.isCompleted) {
                // cancel waiting senders
                ready.completeExceptionally(CancellationException("Disconnected: $reason"))
            }
            gatt?.let {
                try {
                    it.disconnect()
                } catch (_: Throwable) {
                }
                try {
                    it.close()
                } catch (_: Throwable) {
                }
            }
        } finally {
            gatt = null
            _connectionState.value = ConnectionState.Disconnected(reason)
            resetReady()
        }
    }

    // ---------- GATT callback ----------
    private val gattCallback = object : BluetoothGattCallback() {

        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(g: BluetoothGatt, status: Int, newState: Int) {
            if (status != BluetoothGatt.GATT_SUCCESS) {
                Log.w(TAG, "onConnectionStateChange status=$status newState=$newState")
                disconnectInternal("GATT error status=$status")
                return
            }

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.i(TAG, "Connected. Discovering services…")
                // Kick service discovery
                g.discoverServices()
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i(TAG, "Disconnected.")
                disconnectInternal("Disconnected by remote")
            }
        }

        @SuppressLint("MissingPermission")
        override fun onServicesDiscovered(g: BluetoothGatt, status: Int) {
            if (status != BluetoothGatt.GATT_SUCCESS) {
                disconnectInternal("Service discovery failed: $status")
                return
            }
            Log.i(TAG, "Services discovered. Requesting MTU $desiredMtu")
            g.requestMtu(desiredMtu)

            // Cache write characteristic
            val service = g.getService(BOON_SERVICE_UUID)
            if (service == null) {
                disconnectInternal("Service not found")
                return
            }
            val map = mutableMapOf<BoonLEDCharacteristic, BluetoothGattCharacteristic>()
            for (target in BoonLEDCharacteristic.entries) {
                val ch = service.getCharacteristic(target.uuid)
                if (ch != null) map[target] = ch
            }

            // Decide readiness: require at least the ones you need for the app to function
            if (!map.containsKey(BoonLEDCharacteristic.LedSet)) {
                disconnectInternal("LedSet characteristic not found")
                return
            }

            characteristicMap = map

            if (!ready.isCompleted) ready.complete(Unit)
            scope.launch {
                ensureReady()
                val snapshot = pendingMutex.withLock { pendingCtrlType.toMap() }
                for ((id, bytes) in snapshot) {
                    Log.i(TAG, "Replaying CtrlTypeSet for ${snapshot.size} controllers")
                    configureController(id, bytes)
                    // plus your “configured gate” marking on write success if you implemented it
                }
            }
        }

        override fun onMtuChanged(g: BluetoothGatt, mtu: Int, status: Int) {
            Log.i(TAG, "MTU changed mtu=$mtu status=$status")
        }

        override fun onCharacteristicWrite(
            g: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            val req = inflightRequest
            if (req == null) {
                Log.w(TAG, "onCharacteristicWrite but inflightRequest was null; status=$status")
                return
            }
            val ok = status == BluetoothGatt.GATT_SUCCESS
            writeAck.trySend(req to ok)
        }

        // Optional reads/notifications -> incoming flow
        override fun onCharacteristicChanged(
            g: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            val bytes = characteristic.value ?: return
            _incoming.tryEmit(bytes)
        }

        override fun onCharacteristicRead(
            g: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray,
            status: Int
        ) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                _incoming.tryEmit(value)
            }
        }
    }

    // ---------- Helpers ----------
    private val requiredChars: Set<BoonLEDCharacteristic> = setOf(
        BoonLEDCharacteristic.LedSet,
        BoonLEDCharacteristic.BrightSet,
        // add others if you truly require them
    )

    private fun isReady(): Boolean {
        val hasGatt = gatt != null
        val hasRequired = requiredChars.all { characteristicMap.containsKey(it) }
        return hasGatt && hasRequired && ready.isCompleted && !ready.isCancelled
    }

    private suspend fun ensureReady() {
        Log.i(TAG, "Ensure Ready")
        while (true) {
            if (stopRequested.get()) throw CancellationException("BLE stopped")
            if (isReady()) return

            try {
                ready.await()
            } catch (e: CancellationException) {
                // disconnected while waiting -> loop and await the next readyField
                continue
            } catch (t: Throwable) {
                // also loop on other transient failures
                continue
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun writeCharacteristicInternal(
        g: BluetoothGatt,
        c: BluetoothGattCharacteristic,
        payload: ByteArray,
        writeType: Int
    ): Boolean {
        val status = g.writeCharacteristic(
            c,
            payload,
            writeType
        )
        return status == BluetoothStatusCodes.SUCCESS
    }

    // We keep `ready` as a var so we can reset it cleanly.
    @Volatile
    private var readyField: CompletableDeferred<Unit> = CompletableDeferred()
    private val ready: CompletableDeferred<Unit> get() = readyField

    private fun resetReady() {
        readyField = CompletableDeferred()
    }

    override suspend fun configureController(controllerId: String, payload: ByteArray) {
        Log.i(TAG, "Got to configureController with Controller $controllerId, payload was ${payload.decodeToString()}")
        pendingMutex.withLock { pendingCtrlType[controllerId] = payload }
        writeQueue.send(
            WriteRequest(
                target = BoonLEDCharacteristic.CtrlTypeSet,
                payload = payload,
                controllerId = controllerId
            )
        )
    }

    private suspend fun ensureConfigured(controllerId: String) {
        while (true) {
            if (configuredControllers.value.contains(controllerId)) return

            // if not configured, resend latest CtrlTypeSet (best-effort)
            val bytes = getPendingPayload(controllerId)

            writeQueue.send(
                WriteRequest(
                    target = BoonLEDCharacteristic.CtrlTypeSet,
                    payload = bytes,
                    controllerId = controllerId
                )
            )

            configuredControllers.filter { it.contains(controllerId) }.first()
            return
        }
    }

    private suspend fun getPendingPayload(controllerId: String): ByteArray {
        val timeoutMs = 3_000L
        val start = System.currentTimeMillis()

        while (true) {
            pendingMutex.withLock {
                pendingCtrlType[controllerId]?.let { return it }
            }
            if (System.currentTimeMillis() - start > timeoutMs) {
                throw IllegalStateException("No CtrlTypeSet payload registered for controllerId=$controllerId")
            }
            delay(50)
        }
    }

    private fun onWriteSucceeded(req: WriteRequest) {
        if (req.target == BoonLEDCharacteristic.CtrlTypeSet) {
            val id = req.controllerId ?: return
            configuredControllers.update { it + id }
        }
    }

    override fun tryConfigureController(controllerId: String, payload: ByteArray): Boolean {
        if (stopRequested.get()) return false
        scope.launch {
            runCatching { configureController(controllerId, payload) }
                .onFailure { Log.w(TAG, "tryConfigureController failed: ${it.message}", it) }
        }
        return true
    }
}
