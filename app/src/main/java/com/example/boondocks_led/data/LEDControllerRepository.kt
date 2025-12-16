package com.example.boondocks_led.data

import android.util.Log
import com.example.boondocks_led.data.Constants.TAG
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class LEDControllerRepository @Inject constructor(
    private val controllerFactory: LEDControllerFactory
) {

    private val controllers = mutableMapOf<String, LEDController>()

    fun get(controllerId: String): LEDController {
        return controllers.getOrPut(controllerId) {
            Log.i(TAG, "Calling create from repository")
            controllerFactory.create(controllerId)
        }
    }
}


class LEDControllerFactory @Inject constructor() {
    fun create(
        controllerId: String,
        controllerName: String = "Controller $controllerId",
        channels: List<String> = listOf("Ch1", "Ch2", "Ch3", "Ch4")
    ): LEDController = LEDController(
        controllerId = controllerId,
        controllerType = ControllerType.RGBW,
        controllerName = controllerName,
        channels = channels,

    )
}