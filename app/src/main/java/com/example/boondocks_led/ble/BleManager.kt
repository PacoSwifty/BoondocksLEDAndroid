package com.example.boondocks_led.ble

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

sealed interface ConnectionState {
    data object Idle : ConnectionState
    data object Scanning : ConnectionState
    data object Connecting : ConnectionState
    data class Connected(val deviceName: String?, val deviceAddress: String?) : ConnectionState
    data class Disconnected(val reason: String? = null) : ConnectionState
    data class Error(val message: String, val throwable: Throwable? = null) : ConnectionState
}

interface BleManager {
    val connectionState: StateFlow<ConnectionState>

    /** Raw incoming bytes from notifications/reads (optional until you enable notifications). */
    val incoming: SharedFlow<ByteArray>

    /** Start “connect & keep connected” loop (auto-reconnect). Safe to call multiple times. */
    suspend fun start()

    /** Stop scanning, disconnect, and stop auto-reconnect loop. */
    suspend fun stop()

    /** Queue bytes to write to the current write characteristic. */
    suspend fun send(characteristic: BoonLEDCharacteristic, bytes: ByteArray)

    /** Convenience: send UTF-8 text */
    suspend fun send(characteristic: BoonLEDCharacteristic, text: String) = send(characteristic, text.toByteArray(Charsets.UTF_8))

    /** Fire-and-forget convenience */
    fun trySend(characteristic: BoonLEDCharacteristic, bytes: ByteArray): Boolean

    /** Convenience: just pass in a string */
    fun trySend(characteristic: BoonLEDCharacteristic, text: String) = trySend(characteristic, text.toByteArray(Charsets.UTF_8))
}