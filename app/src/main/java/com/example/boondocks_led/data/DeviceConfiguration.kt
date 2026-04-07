package com.example.boondocks_led.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChannelNames(
    @SerialName("R") val r: String = "",
    @SerialName("G") val g: String = "",
    @SerialName("B") val b: String = "",
    @SerialName("W") val w: String = ""
)

@Serializable
data class ControllerConfig(
    @SerialName("Name") val name: String,
    @SerialName("Type") val type: ControllerType,
    @SerialName("ChanNames") val channelNames: ChannelNames
)

@Serializable
data class DeviceConfiguration(
    @SerialName("1") val controller1: ControllerConfig? = null,
    @SerialName("2") val controller2: ControllerConfig? = null,
    @SerialName("3") val controller3: ControllerConfig? = null,
    @SerialName("4") val controller4: ControllerConfig? = null,
    @SerialName("Scenes") val scenes: Map<String, String>? = null
) {
    fun getController(id: String): ControllerConfig? = when (id) {
        "1" -> controller1
        "2" -> controller2
        "3" -> controller3
        "4" -> controller4
        else -> null
    }
}

fun getDefaultConfiguration(): DeviceConfiguration = DeviceConfiguration(
    controller1 = ControllerConfig(
        name = "DEFAULT 1",
        type = ControllerType.RGBW,
        channelNames = ChannelNames(r = "Red", g = "Green", b = "Blue", w = "White")
    ),
    controller2 = ControllerConfig(
        name = "DEFAULT 2",
        type = ControllerType.RGBPLUS1,
        channelNames = ChannelNames(r = "Red", g = "Green", b = "Blue", w = "White")
    ),
    controller3 = ControllerConfig(
        name = "DEFAULT 3",
        type = ControllerType.FOURCHANNEL,
        channelNames = ChannelNames(r = "Chan1", g = "Chan2", b = "Chan3", w = "Chan4")
    ),
    controller4 = ControllerConfig(
        name = "DEFAULT 4",
        type = ControllerType.RGBW,
        channelNames = ChannelNames(r = "Red", g = "Green", b = "Blue", w = "White")
    ),
    scenes = mapOf("1" to "Scene 1", "2" to "Scene 2", "3" to "Scene 3", "4" to "Scene 4")
)
