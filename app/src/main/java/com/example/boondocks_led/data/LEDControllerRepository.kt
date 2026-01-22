package com.example.boondocks_led.data

import android.util.Log
import com.example.boondocks_led.ble.BleManager
import com.example.boondocks_led.data.Constants.TAG
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class LEDControllerRepository @Inject constructor(
    private val controllerFactory: LEDControllerFactory
) {

    private val controllers = mutableMapOf<String, LEDController>()

    fun get(controllerId: String, type: ControllerType): LEDController {
        return controllers.getOrPut(controllerId) {
            Log.i(TAG, "Calling create from repository")
            controllerFactory.create(controllerId, type=type)
        }
    }

    /** Turns off all controllers - sends BLE command and updates local state */
    fun turnOffAll() {
        controllers.values.firstOrNull()?.turnOffLights()
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
        controllerType = type,
        controllerName = controllerName,
        ble = bleManager
    )
}