package com.example.boondocks_led.data

import android.util.Log
import com.example.boondocks_led.ble.BleManager
import com.example.boondocks_led.ble.BoonLEDCharacteristic
import com.example.boondocks_led.data.Constants.TAG
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class LEDControllerRepository @Inject constructor(
    private val controllerFactory: LEDControllerFactory,
    private val bleManager: BleManager
) {

    private val controllers = mutableMapOf<String, LEDController>()

    fun get(controllerId: String, type: ControllerType, name: String = "Controller $controllerId"): LEDController {
        return controllers.getOrPut(controllerId) {
            Log.i(TAG, "Calling create from repository")
            controllerFactory.create(controllerId, controllerName = name, type = type)
        }
    }

    /** Turns off all controllers - sends BLE command and updates local state */
    fun turnOffAll() {
        val msg = Json.encodeToString(mapOf(1 to "off"))
        bleManager.trySend(BoonLEDCharacteristic.AllOff, msg)
        controllers.values.forEach { it.turnOffState() }
    }
}


class LEDControllerFactory @Inject constructor(
    private val bleManager: BleManager
) {
    fun create(
        controllerId: String,
        controllerName: String = "Controller $controllerId",
        type: ControllerType,
    ): LEDController = LEDController(
        controllerId = controllerId,
        initialType = type,
        controllerName = controllerName,
        ble = bleManager
    )
}