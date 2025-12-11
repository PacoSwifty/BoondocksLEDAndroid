package com.example.boondocks

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.boondocks.data.Constants.ANTARCTICA
import com.example.boondocks.ui.components.TabRow
import com.example.boondocks.ui.navigation.BoondocksNavHost
import com.example.boondocks.ui.navigation.Lights
import com.example.boondocks.ui.navigation.navigateSingleTopTo
import com.example.boondocks.ui.navigation.tabRowScreens
import com.example.boondocks.ui.theme.BoondocksTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.UUID

//This is the OG Pico Board that we POC'd on
//const val MICROPY_UUID = "6E400001-B5A3-F393-E0A9-E50E24DCCA9E"
//const val MICROPY_RX_UUID = "6e400002-b5a3-f393-e0a9-e50e24dcca9e" // Write to this UUID
//const val MICROPY_TX_UUID = "6e400003-b5a3-f393-e0a9-e50e24dcca9e" // Read from this UUID


//These are UUID's for the Controller Board and characteristics we might want to write to or read from
const val BOON_SERVICE_UUID = "b00d0c55-1111-2222-3333-0000b00d0c50"
const val CHARACTERISTIC_READ_CONFIG = "b00d0c55-1111-2222-3333-0000b00d0c51"
const val CHARACTERISTIC_LED_SET = "b00d0c55-1111-2222-3333-0000b00d0c52"
const val CHARACTERISTIC_BRIGHT_SET = "b00d0c55-1111-2222-3333-0000b00d0c53"
const val CHARACTERISTIC_ALL_OFF = "b00d0c55-1111-2222-3333-0000b00d0c54"
const val CHARACTERISTIC_SCENE_SELECT = "b00d0c55-1111-2222-3333-0000b00d0c55"
const val CHARACTERISTIC_SCENE_SAVE = "b00d0c55-1111-2222-3333-0000b00d0c56"
const val CHARACTERISTIC_CTRL_TYPE_SET = "b00d0c55-1111-2222-3333-0000b00d0c57"


const val BT_TAG = "Bluetooth"


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    //region bluetooth variables

    private val bluetoothPermissions = arrayOf(
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    private val bluetoothLeScanner: BluetoothLeScanner? by lazy {
        bluetoothAdapter?.bluetoothLeScanner
    }

    private var bluetoothGatt: BluetoothGatt? = null
    private var targetDevice: BluetoothDevice? = null
    private var targetWriteCharacteristic: BluetoothGattCharacteristic? = null
    private var targetReadCharacteristic: BluetoothGattCharacteristic? = null

    private val bluetoothRequestCode = 1001

    //endregion


    private val mainActivityViewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BoondocksApp()
        }

        //todo uncomment this and add a loading indicator
        requestBluetoothPermissions()
        collectLightsMessage()

    }

    private fun collectLightsMessage() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainActivityViewModel.lightsMessageFlow.collect {
                    Log.i(ANTARCTICA, "received message from Lights Flow: $it")
                    sendMessage(it)
                }
            }
        }
    }

    @Composable
    fun BoondocksApp() {
        val navController = rememberNavController()
        val currentBackStack by navController.currentBackStackEntryAsState()
        val currentDestination = currentBackStack?.destination

        AppScreenContent(navController, currentBackStack, currentDestination)
    }

    @Composable
    fun AppScreenContent(
        navController: NavHostController,
        currentBackStack: NavBackStackEntry?,
        currentDestination: NavDestination?
    ) {
        BoondocksTheme {
            val currentScreen =
                tabRowScreens.find { it.route == currentDestination?.route } ?: Lights
            Scaffold(
                topBar = {
                    Column() {
                        Box(
                            modifier = Modifier
                                .statusBarsPadding()
                                .background(Color.Yellow)
                        )
                        TabRow(
                            allScreens = tabRowScreens,
                            onTabSelected = { newScreen ->
                                navController.navigateSingleTopTo(newScreen.route)
                            },
                            currentScreen = currentScreen,
                        )
                    }
                }

            ) { innerPadding ->
                BoondocksNavHost(
                    navController = navController,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }


    /** ---------- Below this Point is all the Bluetooth Code ---------- **/
//region bluetooth
    private val scanCallback = object : ScanCallback() {
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)

            Log.i(BT_TAG, "SINGLE SCAN RESULT HIT")
            val device: BluetoothDevice? = result?.device
            val deviceName = device?.name ?: "Unknown Device"
            val deviceAddress = device?.address

            //Get UUIDs from scan record
            val serviceUuids = result?.scanRecord?.serviceUuids
            if (serviceUuids != null && serviceUuids.isNotEmpty()) {
                serviceUuids.forEach { uuid ->
                    Log.d(BT_TAG, "Device: $deviceName,Service UUID: ${uuid.uuid}")
                }
            } else {
                Log.d(BT_TAG, "Device: $deviceName, NO SERVICE UUIDs advertised!")
            }

            Log.d(BT_TAG, "Found device: $deviceName, Address: $deviceAddress")
            //todo: display list of devices and let user pick one, or decide if we can hardcode it?

            //todo remove hardcode device selection
            if (deviceName == "BoonLED") {
                targetDevice = device
                stopBluetoothScan()
                connectToDevice(device)
            }
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            Log.e(BT_TAG, "Scan failed with error code: $errorCode")
        }
    }

    private val gattCallback = object : BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.i(BT_TAG, "Connected to GATT server.")
                // Attempts to discover services after successful connection.
                Log.i(
                    BT_TAG,
                    "Attempting to start service discovery: ${bluetoothGatt?.discoverServices()}"
                )

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i(BT_TAG, "Disconnected from GATT server.")
                startBluetoothScan()
            }
        }


        @SuppressLint("MissingPermission")
        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            super.onServicesDiscovered(gatt, status)
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.i(BT_TAG, "Services discovered.")
                Log.i(BT_TAG, "Requesting MTU: 512")
                bluetoothGatt?.requestMtu(512)

                // Find the service and characteristic you want to use
                //todo figure out appropriate long-term values for these
                findTargetCharacteristic(gatt)
                if (targetReadCharacteristic != null) {
                    Log.i(BT_TAG, "Characteristic found. Target Read is not null.")
                    //todo uncomment this and set up the read characteristic in the below, commented out, method
//                    setCharacteristicNotification(targetReadCharacteristic!!, true)
                } else {
                    Log.e(BT_TAG, "Characteristic not found.")
                }
            } else {
                Log.w(BT_TAG, "onServicesDiscovered received: $status")
            }
        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            super.onCharacteristicWrite(gatt, characteristic, status)
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.i(BT_TAG, "Characteristic write successful.")
            } else {
                Log.e(BT_TAG, "Characteristic write failed with status: $status")
            }
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray,
            status: Int
        ) {
            super.onCharacteristicRead(gatt, characteristic, value, status)
            val receivedMessage = String(value, Charsets.UTF_8)
            Log.i(BT_TAG, "Received READ Message $receivedMessage")
        }

        //todo verify if this deprecated method is getting hit and, if not, delete it
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            super.onCharacteristicChanged(gatt, characteristic)
            val receivedMessage = String(characteristic.value, Charsets.UTF_8)
            Log.i(BT_TAG, "Received CHANGED Message $receivedMessage")
        }

        override fun onMtuChanged(gatt: BluetoothGatt, mtu: Int, status: Int) {
            super.onMtuChanged(gatt, mtu, status)
            Log.i(BT_TAG, "MTU CHANGED: mtu = $mtu status = $status")
        }
    }

    private fun requestBluetoothPermissions() {
        if (bluetoothPermissions.all {
                ActivityCompat.checkSelfPermission(
                    this,
                    it
                ) == PackageManager.PERMISSION_GRANTED
            }) {
            startBluetoothScan()
        } else {
            ActivityCompat.requestPermissions(this, bluetoothPermissions, bluetoothRequestCode)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == bluetoothRequestCode) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                // Permissions granted, proceed with Bluetooth operations
                startBluetoothScan()
            } else {
                // todo Permissions denied, handle accordingly (e.g., show a message)
                Toast.makeText(this, "Need Bluetooth Permissions", Toast.LENGTH_LONG).show()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun startBluetoothScan() {
        Log.i(ANTARCTICA, "starting bluetooth scan")
        bluetoothLeScanner?.startScan(scanCallback)

    }

    @SuppressLint("MissingPermission")
    private fun stopBluetoothScan() {
        Log.i(ANTARCTICA, "stopping bluetooth scan")
        bluetoothLeScanner?.stopScan(scanCallback)
    }

    @SuppressLint("MissingPermission")
    private fun connectToDevice(device: BluetoothDevice?) {
        bluetoothGatt = device?.connectGatt(this, false, gattCallback)
    }

    @SuppressLint("MissingPermission")
    fun sendMessage(message: String) {
        if (targetWriteCharacteristic == null) {
            Log.e(BT_TAG, "Characteristic not found.")
//            todo: remove this toast
            Toast.makeText(
                this,
                "Whoops, something went wrong with the BT Characteristic.",
                Toast.LENGTH_SHORT
            ).show()
            return
        } else {
            // Convert the message to bytes
            val messageBytes = message.toByteArray(Charsets.UTF_8)
            var currCharacteristic = targetWriteCharacteristic
            if (currCharacteristic != null) {
                Log.i(BT_TAG, "message: $message")
                Log.i(BT_TAG, "message bytes: $messageBytes")
                bluetoothGatt?.writeCharacteristic(
                    currCharacteristic,
                    messageBytes,
                    BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
                )
            }
        }
    }

    //todo uncomment this to be able to read messages back from the board (I think?)
//    @SuppressLint("MissingPermission")
//    private fun setCharacteristicNotification(
//        characteristic: BluetoothGattCharacteristic,
//        enabled: Boolean
//    ) {
//        MICROPY_TX_UUID
//        bluetoothGatt?.let { gatt ->
//            gatt.setCharacteristicNotification(characteristic, enabled)
//            if (characteristic.uuid == UUID.fromString(MICROPY_TX_UUID)) {
//                val descriptor = characteristic.getDescriptor(UUID.fromString(MICROPY_TX_UUID))
//                //todo note to self - descriptor is null because found characteristic doesn't match TX UUID
//                if (descriptor != null) {
//                    descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
//                    gatt.writeDescriptor(descriptor)
//                } else {
//                    Log.e(BT_TAG, "Descriptor not found.")
//                }
//            } else {
//                Log.e(BT_TAG, "BluetoothGatt not initialized.")
//            }
//        }
//    }

    /**
     * BluetoothGatt has a list of services, which each have a list of characteristics.
     * Find the service that matches our Pico's UUID, then find its characteristic that has
     * the UUID we'd like to write to. Return it. *note* the UUID's are very similar, but slightly different.
     *
     * //todo set up the new characteristics from Boon Json Messages
     */
    private fun findTargetCharacteristic(gatt: BluetoothGatt) {
        for (service in gatt.services) {
            Log.i(BT_TAG, "Service UUID: ${service.uuid}")
            if (service.uuid.toString().equals(BOON_SERVICE_UUID, ignoreCase = true)) {
                for (characteristic in service.characteristics) {
                    Log.i(BT_TAG, "Characteristic UUID: ${characteristic.uuid}")
                    if (characteristic.uuid.toString().equals(CHARACTERISTIC_LED_SET, ignoreCase = true)) {
                        targetWriteCharacteristic = characteristic
                    }
//                    if (characteristic.uuid.toString().equals(MICROPY_TX_UUID, ignoreCase = true)) {
//                        targetReadCharacteristic = characteristic
//                    }
                }
            }
        }
    }

    //endregion
}