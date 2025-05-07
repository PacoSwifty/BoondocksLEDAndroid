package com.example.boondocks.ui.lights.toggleLights

import com.example.boondocks.data.Constants
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class DriverLightToggleMessage(
    @SerialName(Constants.DRIVER_LIGHTS)
    val message: String
) {
    override fun toString(): String {
        return Json.encodeToString(this)
    }
}

@Serializable
data class PassengerLightToggleMessage(
    @SerialName(Constants.PASSENGER_LIGHTS)
    val message: String
) {
    override fun toString(): String {
        return Json.encodeToString(this)
    }
}

@Serializable
data class WorkLightToggleMessage(
    @SerialName(Constants.WORK_LIGHTS)
    val message: String
) {
    override fun toString(): String {
        return Json.encodeToString(this)
    }
}

@Serializable
data class LightBarToggleMessage(
    @SerialName(Constants.LIGHTBAR)
    val message: String
) {
    override fun toString(): String {
        return Json.encodeToString(this)
    }
}
