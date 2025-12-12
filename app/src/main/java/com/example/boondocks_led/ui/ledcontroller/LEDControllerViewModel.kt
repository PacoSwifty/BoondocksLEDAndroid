package com.example.boondocks_led.ui.ledcontroller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.boondocks_led.data.LEDController
import com.example.boondocks_led.data.LEDControllerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LEDControllerViewModel @Inject constructor(
    private val ledControllerRepository: LEDControllerRepository,
) : ViewModel() {

    private var controller: LEDController? = null
    private val _uiState = MutableStateFlow<LEDControllerState?>(null)
    val uiState : StateFlow<LEDControllerState?> = _uiState

    fun init(controllerId: String) {
        if (controller != null) return
        controller = ledControllerRepository.get(controllerId)

        viewModelScope.launch {
            controller!!.state.collect { _uiState.value = it }
        }
    }


    fun setColor(r: Int, g: Int, b: Int, w: Int) {
        controller?.setRgbw(r, g, b, w)
    }

    fun setBrightness(value: Int) {
        controller?.setBrightness(value)
    }
}

