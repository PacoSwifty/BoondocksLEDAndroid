package com.example.boondocks_led.data

import com.example.boondocks_led.ui.ledcontroller.LEDControllerState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject


enum class ControllerType {
    RGBW,
    RGBPLUS1,
    FOURCHANNEL
}

data class Channel(val name: String, val value: Int)

class LEDController @Inject constructor(
    var controllerId: String,
    var controllerType: ControllerType = ControllerType.RGBW,
    var controllerName: String = "",
    var channels: List<String>
) {

    //todo initialize channels if we use those
    private val _state = MutableStateFlow(LEDControllerState(controllerId = controllerId, name = controllerName, type = controllerType))
    val state: StateFlow<LEDControllerState> = _state.asStateFlow()

    fun setRgbw(r: Int, g: Int, b: Int, w: Int) {
        _state.update { it.copy(r = r, g = g, b = b, w = w) }
        //todo do something, maybe emit to mainactivitys characteristics?
    }

    fun setBrightness(value: Int) {
        _state.update { it.copy(brightness = value.coerceIn(0, 255)) }
    }
}



    /** needs to have a type, a name, a list of channels, those channels need to be nameable objects */

