package com.example.boondocks_led.data

import com.example.boondocks_led.ble.BleManager
import com.example.boondocks_led.ble.BoonLEDCharacteristic
import com.example.boondocks_led.ui.ledcontroller.LEDControllerState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.json.Json
import javax.inject.Inject


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

    //todo initialize channels if we use those
    private val _state = MutableStateFlow(LEDControllerState(
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
        rgbwBrightness = 0f))
    val state: StateFlow<LEDControllerState> = _state.asStateFlow()


    fun turnOffLights() {
        ble.trySend(BoonLEDCharacteristic.AllOff, buildAllOffMessage())
    }
    //region set brightnesses / toggles from viewmodel
    fun setRGBColor(r: Int, g: Int, b: Int, w: Int) {
        //todo maybe mutually exclusive logic here?
        _state.update { it.copy(r = r, g = g, b = b, w = w) }
        ble.trySend(BoonLEDCharacteristic.LedSet, buildSetRGBMessage(r, g, b))
    }

    fun setRGBEnabled(enabled: Boolean) {
        _state.update { it.copy(isRGBWOn = enabled) }
    }

    fun setRGBBrightness(brightness: Float) {
        _state.update { it.copy(rgbwBrightness = brightness) }
    }

    fun setPlusOneBrightness(brightness: Float) {
        _state.update { it.copy(plusOneBrightness = brightness) }
    }

    fun setPlusOneEnabled(enabled: Boolean) {
        _state.update { it.copy(isPlusOneOn = enabled) }
    }

    fun setChannelEnabled(index: Int, enabled: Boolean) {
        when(index) {
            1 -> _state.update { it.copy(isFourChanOneOn = enabled) }
            2 -> _state.update { it.copy(isFourChanTwoOn = enabled) }
            3 -> _state.update { it.copy(isFourChanThreeOn = enabled) }
            4 -> _state.update { it.copy(isFourChanFourOn = enabled) }
        }
    }

    fun setChannelBrightness(index: Int, brightness:Float) {
        when(index) {
            1 -> _state.update { it.copy(fourChanOneBrightness = brightness) }
            2 -> _state.update { it.copy(fourChanTwoBrightness = brightness) }
            3 -> _state.update { it.copy(fourChanThreeBrightness = brightness) }
            4 -> _state.update { it.copy(fourChanFourBrightness = brightness) }
        }
    }
    //endregion

    //region Build JSON Messages
    fun buildSetRGBMessage(r : Int, g : Int, b : Int) : String {
        val cmd = mapOf(controllerId to RGBW(r, g, b, 0))
        return Json.encodeToString(cmd)
    }

    fun buildAllOffMessage() : String {
        val cmd = mapOf(1 to "off")
        return Json.encodeToString(cmd)
    }

    //endregion
}
