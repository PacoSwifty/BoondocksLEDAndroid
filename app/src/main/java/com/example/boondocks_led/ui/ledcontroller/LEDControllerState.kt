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
    val type: ControllerType = ControllerType.RGBW
    //Considerations
    // brightness might be a range 0-100
    // might have an array of channels rather than hardcoded rgb?
    //val lastError: String? = null
    //val isConnected: Boolean = false
)