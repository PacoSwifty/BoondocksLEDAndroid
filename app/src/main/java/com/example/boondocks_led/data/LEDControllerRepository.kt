package com.example.boondocks_led.data

import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class LEDControllerRepository @Inject constructor(
    private val controllerFactory: LEDControllerFactory
) {

    private val controllers = mutableMapOf<String, LEDController>()

    fun get(controllerId: String): LEDController {
        return controllers.getOrPut(controllerId) {
            controllerFactory.create(controllerId)
        }
    }
}


class LEDControllerFactory @Inject constructor() {
    fun create(
        controllerId: String,
        controllerName: String = "Me llamo controller $controllerId",
        channels: List<String> = listOf("Ch1", "Ch2", "Ch3", "Ch4")
    ): LEDController = LEDController(
        controllerId = controllerId,
        controllerType = ControllerType.RGBW,
        controllerName = controllerName,
        channels = channels,

    )
}