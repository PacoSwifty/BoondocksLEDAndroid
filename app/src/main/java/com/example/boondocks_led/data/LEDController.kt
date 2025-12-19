package com.example.boondocks_led.data

import android.util.Log
import com.example.boondocks_led.ble.BleManager
import com.example.boondocks_led.ble.BoonLEDCharacteristic
import com.example.boondocks_led.data.Constants.TAG
import com.example.boondocks_led.ui.ledcontroller.LEDChannel
import com.example.boondocks_led.ui.ledcontroller.LEDControllerState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.json.Json
import javax.inject.Inject
import kotlin.math.roundToInt


enum class ControllerType {
    RGBW,
    RGBPLUS1,
    FOURCHANNEL
}


class LEDController @Inject constructor(
    val controllerId: String,
    var controllerType: ControllerType = ControllerType.RGBW,
    var controllerName: String = "",
    val ble: BleManager

) {

    private val _state = MutableStateFlow(
        LEDControllerState(
            controllerId = controllerId,
            name = controllerName,
            type = controllerType,
            r = 255,
            g = 0,
            b = 0,
            w = 0,
            isRGBWOn = false,
            isPlusOneOn = false,
            isFourChanOneOn = false,
            isFourChanTwoOn = false,
            isFourChanThreeOn = false,
            isFourChanFourOn = false,
            plusOneBrightness = 0f,
            fourChanOneBrightness = 0f,
            fourChanTwoBrightness = 0f,
            fourChanThreeBrightness = 0f,
            fourChanFourBrightness = 0f,
            rgbwBrightness = 0f
        )
    )
    val state: StateFlow<LEDControllerState> = _state.asStateFlow()


    fun turnOffLights() {
        ble.trySend(BoonLEDCharacteristic.AllOff, buildAllOffMessage())
    }

    //region set brightnesses / toggles from viewmodel
    fun setRGBColor(r: Int, g: Int, b: Int, w: Int) {
        //todo maybe mutually exclusive logic here?
        _state.update { it.copy(r = r, g = g, b = b, w = w) }
        ble.trySend(BoonLEDCharacteristic.LedSet, buildSetRGBWMessage(r, g, b))
    }

    fun setRGBEnabled(enabled: Boolean) {
        _state.update { it.copy(isRGBWOn = enabled) }
    }

    fun setRGBBrightness(brightness: Float) {
        val hardStepBrightness = (brightness * 3).roundToInt()
        Log.i(TAG, "Brightness should map to: $hardStepBrightness")
        _state.update { it.copy(rgbwBrightness = brightness) }
    }

    fun commitBrightnessChange(channel: LEDChannel) {
        val s = state.value  // latest state after drag

        when (channel) {
            LEDChannel.RGB -> {
                // build + send RGBW brightness message based on s.rgbwBrightness
                ble.trySend(
                    BoonLEDCharacteristic.BrightSet,
                    text =
                        if (controllerType == ControllerType.RGBW) buildSetRGBWBrightnessMessage(s.rgbwBrightness) //RGBW message
                        else buildSetRGBBrightnessMessage(s.rgbwBrightness) // Just RGB message for +1 controllers
                )
            }

            LEDChannel.PLUS_ONE -> {
                ble.trySend(
                    BoonLEDCharacteristic.BrightSet,
                    buildSetPlusOneBrightnessMessage("W", s.plusOneBrightness)
                )
            }
            LEDChannel.CH1 -> {

                ble.trySend(
                    BoonLEDCharacteristic.BrightSet,
                    buildSetPlusOneBrightnessMessage("R", s.fourChanOneBrightness)
                )
            }

            LEDChannel.CH2 -> {

                ble.trySend(
                    BoonLEDCharacteristic.BrightSet,
                    buildSetPlusOneBrightnessMessage("G", s.fourChanTwoBrightness)
                )
            }

            LEDChannel.CH3 -> {

                ble.trySend(
                    BoonLEDCharacteristic.BrightSet,
                    buildSetPlusOneBrightnessMessage("B", s.fourChanThreeBrightness)
                )
            }

            LEDChannel.CH4 -> {

                ble.trySend(
                    BoonLEDCharacteristic.BrightSet,
                    buildSetPlusOneBrightnessMessage("W", s.fourChanFourBrightness)
                )
            }
        }
    }

    fun setPlusOneBrightness(brightness: Float) {
        _state.update { it.copy(plusOneBrightness = brightness) }
    }

    fun setPlusOneEnabled(enabled: Boolean) {
        _state.update { it.copy(isPlusOneOn = enabled) }
    }

    fun setChannelEnabled(index: Int, enabled: Boolean) {
        when (index) {
            1 -> _state.update { it.copy(isFourChanOneOn = enabled) }
            2 -> _state.update { it.copy(isFourChanTwoOn = enabled) }
            3 -> _state.update { it.copy(isFourChanThreeOn = enabled) }
            4 -> _state.update { it.copy(isFourChanFourOn = enabled) }
        }
    }

    fun setChannelBrightness(index: Int, brightness: Float) {
        when (index) {
            1 -> _state.update { it.copy(fourChanOneBrightness = brightness) }
            2 -> _state.update { it.copy(fourChanTwoBrightness = brightness) }
            3 -> _state.update { it.copy(fourChanThreeBrightness = brightness) }
            4 -> _state.update { it.copy(fourChanFourBrightness = brightness) }
        }
    }
    //endregion

    //region Build JSON Messages
    /**
     * Documentation for how messages should be built can be found in the JsonMessages.kt file or
     * at https://github.com/tswift123/led_controller/blob/main/Json%20messages.txt
     */
    fun buildSetRGBWMessage(r: Int, g: Int, b: Int): String {
        val cmd = mapOf(controllerId to RGBW(r, g, b, 0))
        return Json.encodeToString(cmd)
    }

    //todo currently the pico supports brightness values of 0-3, update this later to pass float of 0.0f - 1.0f
    fun buildSetRGBWBrightnessMessage(brightness: Float): String {
        val hardStepBrightness = (brightness * 3).roundToInt()
        val cmd = mapOf(
            controllerId to RGBW(
                hardStepBrightness,
                hardStepBrightness,
                hardStepBrightness,
                hardStepBrightness
            )
        )
        return Json.encodeToString(cmd)
    }

    fun buildSetRGBBrightnessMessage(brightness: Float): String {
        val hardStepBrightness = (brightness * 3).roundToInt()
        val cmd =
            mapOf(controllerId to RGB(hardStepBrightness, hardStepBrightness, hardStepBrightness))
        return Json.encodeToString(cmd)
    }

    /** Here channel can be one of R, G, B, or W */
    fun buildSetPlusOneBrightnessMessage(channel: String, brightness: Float): String {
        val hardStepBrightness = (brightness * 3).roundToInt()
        val cmd = mapOf(controllerId to SingleChannelChange("W", hardStepBrightness))
        return Json.encodeToString(cmd)
    }

    fun buildAllOffMessage(): String {
        val cmd = mapOf(1 to "off")
        return Json.encodeToString(cmd)
    }

    //endregion
}
