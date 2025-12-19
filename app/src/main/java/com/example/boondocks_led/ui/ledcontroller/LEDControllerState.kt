package com.example.boondocks_led.ui.ledcontroller

import com.example.boondocks_led.data.ControllerType

data class LEDControllerState(
    val controllerId: String,
    val name: String,
    val r: Int = 0,
    val g: Int = 0,
    val b: Int = 0,
    val w: Int = 0,
    val type: ControllerType = ControllerType.RGBW,
    val isRGBWOn: Boolean,
    val isPlusOneOn: Boolean,
    val isFourChanOneOn: Boolean,
    val isFourChanTwoOn: Boolean,
    val isFourChanThreeOn: Boolean,
    val isFourChanFourOn: Boolean,
    val rgbwBrightness: Float,
    val plusOneBrightness: Float,
    val fourChanOneBrightness: Float,
    val fourChanTwoBrightness: Float,
    val fourChanThreeBrightness: Float,
    val fourChanFourBrightness: Float

    //    val brightness: Int = 255,

    //Considerations
    // might have an array of channels rather than hardcoded rgb?
    //val lastError: String? = null
    //val isConnected: Boolean = false
)

public val previewState = LEDControllerState(
    controllerId = "1",
    name = "Controller 1",
    r = 255,
    g = 0,
    b = 0,
    w = 0,
    type = ControllerType.RGBW,
    isRGBWOn = true,
    isPlusOneOn = false,
    isFourChanOneOn = false,
    isFourChanTwoOn = false,
    isFourChanThreeOn = false,
    isFourChanFourOn = false,
    rgbwBrightness = 0.6f,
    plusOneBrightness = 0f,
    fourChanOneBrightness = 0f,
    fourChanTwoBrightness = 0f,
    fourChanThreeBrightness = 0f,
    fourChanFourBrightness = 0f
)