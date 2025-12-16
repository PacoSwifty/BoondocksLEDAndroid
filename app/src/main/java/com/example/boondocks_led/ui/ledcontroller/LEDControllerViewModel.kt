package com.example.boondocks_led.ui.ledcontroller

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.boondocks_led.ble.BoonLEDCharacteristic
import com.example.boondocks_led.data.BoonApiMessage
import com.example.boondocks_led.data.Constants.TAG
import com.example.boondocks_led.data.LEDController
import com.example.boondocks_led.data.LEDControllerRepository
import com.example.boondocks_led.data.LightsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LEDControllerViewModel @Inject constructor(
    private val ledControllerRepository: LEDControllerRepository,
    private val lightsRepository: LightsRepository
) : ViewModel() {

    private var controller: LEDController? = null
    private val _uiState = MutableStateFlow<LEDControllerState?>(null)
    val uiState : StateFlow<LEDControllerState?> = _uiState

    fun init(controllerId: String) {
        if (controller != null) return
        Log.i(TAG, "Calling get from the viewModel")
        controller = ledControllerRepository.get(controllerId)

        viewModelScope.launch {
            controller!!.state.collect { _uiState.value = it }
        }
    }

    fun onColorSelected(r: Int, g: Int, b: Int) {
        Log.i(TAG, "Selected color R:$r, G:$g, B:$b")
//        val testBoardMessage = "{\"4\": {\"R\":$r, \"G\":$g, \"B\":$b, \"W\":0}}"
//        val testBoardMessage = "{\"${controller?.controllerId}\": {\"R\":$r, \"G\":$g, \"B\":$b, \"W\":0}}"
        val testBoardMessage = "{\"${controller?.controllerId}\": {\"R\":255, \"G\":0, \"B\":0, \"W\":0}}"


        val apiMessage = BoonApiMessage(BoonLEDCharacteristic.LedSet, testBoardMessage)
        viewModelScope.launch {
            lightsRepository.emitSetLightMessage(apiMessage)
        }

    }


    //todo consider updating these in the controller itself? see bottom placeholder methods
    fun onSliderChanged(newPosition: Float) {
        Log.i(TAG, "Slider Changed to: $newPosition")
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState?.copy(brightnessSliderValue = newPosition)
            }
        }
    }

    fun onToggleChanged(toggled: Boolean) {
        Log.i(TAG, "Toggle Changed to: $toggled")
    //{'1':'off'}
        val status = if(toggled) "on" else "off"
        val json = "{1:\"${status}\"}"
        val message = BoonApiMessage(BoonLEDCharacteristic.AllOff, json)
        Log.i(TAG,"Emitting message: $json")

        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState?.copy(isLightOn = toggled)
            }
            lightsRepository.emitAllOffMessage(message)
        }
    }


    fun setColor(r: Int, g: Int, b: Int, w: Int) {
        controller?.setRgbw(r, g, b, w)
    }

    fun setBrightness(value: Int) {
        controller?.setBrightness(value)
    }
}

