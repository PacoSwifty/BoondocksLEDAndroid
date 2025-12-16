package com.example.boondocks_led.ui.ledcontroller

import com.example.boondocks_led.data.ControllerType

data class LEDControllerState(
    val controllerId: String,
    val name: String,
    val r: Int = 0,
    val g: Int = 0,
    val b: Int = 0,
    val w: Int = 0,
    val brightness: Int = 255,
    val type: ControllerType = ControllerType.RGBW,
    val isLightOn: Boolean,
    val brightnessSliderValue: Float
    //Considerations
    // might have an array of channels rather than hardcoded rgb?
    //val lastError: String? = null
    //val isConnected: Boolean = false
)