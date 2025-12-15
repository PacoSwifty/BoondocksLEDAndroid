package com.example.boondocks_led.ui.ledcontroller

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.boondocks_led.data.Constants.TAG
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
        Log.i(TAG, "Calling get from the viewModel")
        controller = ledControllerRepository.get(controllerId)

        viewModelScope.launch {
            controller!!.state.collect { _uiState.value = it }
        }
    }

    fun onColorSelected(r: Int, g: Int, b: Int) {
        Log.i(TAG, "Selected color R:$r, G:$g, B:$b")
    }


    fun setColor(r: Int, g: Int, b: Int, w: Int) {
        controller?.setRgbw(r, g, b, w)
    }

    fun setBrightness(value: Int) {
        controller?.setBrightness(value)
    }
}

