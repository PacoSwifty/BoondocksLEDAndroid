package com.example.boondocks.ui.lights

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LightsSceneMessage(

    @SerialName("LEDScene")
    val scene: Int
)
