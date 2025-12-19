package com.example.boondocks_led.ui.ledcontroller

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.boondocks_led.data.Constants.TAG
import com.example.boondocks_led.data.ControllerType
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

    fun init(controllerId: String, type: ControllerType) {
        if (controller != null) return
        Log.i(TAG, "Calling get from the viewModel")
        controller = ledControllerRepository.get(controllerId, type)

        viewModelScope.launch {
            controller!!.state.collect { _uiState.value = it }
        }
    }

    fun onAllOffClicked() {
        controller?.turnOffLights()
    }
    fun onColorSelected(r: Int, g: Int, b: Int) {
        Log.i(TAG, "Selected color R:$r, G:$g, B:$b")
        //todo verify that we can always set white to 0 when picking a color
        controller?.setRGBColor(r,g,b,0)

    }



    fun onToggleChanged(channel: LEDChannel, enabled: Boolean) {
        val c = controller ?: return

        when (channel) {
            LEDChannel.RGB      -> c.setRGBEnabled(enabled)
            LEDChannel.PLUS_ONE -> c.setPlusOneEnabled(enabled)

            LEDChannel.CH1 -> c.setChannelEnabled(index = 1, enabled = enabled)
            LEDChannel.CH2 -> c.setChannelEnabled(index = 2, enabled = enabled)
            LEDChannel.CH3 -> c.setChannelEnabled(index = 3, enabled = enabled)
            LEDChannel.CH4 -> c.setChannelEnabled(index = 4, enabled = enabled)
        }
    }

    fun onBrightnessChanged(channel: LEDChannel, brightness: Float) {
        val c = controller ?: return
        val b = brightness.coerceIn(0f, 1f) // or 0..255 depending on your UI scale

        when (channel) {
            LEDChannel.RGB      -> c.setRGBBrightness(b)
            LEDChannel.PLUS_ONE -> c.setPlusOneBrightness(b)

            LEDChannel.CH1 -> c.setChannelBrightness(index = 1, brightness = b)
            LEDChannel.CH2 -> c.setChannelBrightness(index = 2, brightness = b)
            LEDChannel.CH3 -> c.setChannelBrightness(index = 3, brightness = b)
            LEDChannel.CH4 -> c.setChannelBrightness(index = 4, brightness = b)
        }
    }

    fun onBrightnessFinished(channel: LEDChannel) {
        val c = controller ?: return

        when (channel) {
            LEDChannel.RGB -> {
                // build + send RGBW brightness message based on s.rgbwBrightness
                c.commitBrightnessChange(LEDChannel.RGB)
            }

            LEDChannel.PLUS_ONE -> {
                c.commitBrightnessChange(LEDChannel.PLUS_ONE)
            }

            LEDChannel.CH1 -> c.commitBrightnessChange(LEDChannel.CH1)
            LEDChannel.CH2 -> c.commitBrightnessChange(LEDChannel.CH2)
            LEDChannel.CH3 -> c.commitBrightnessChange(LEDChannel.CH3)
            LEDChannel.CH4 -> c.commitBrightnessChange(LEDChannel.CH4)
        }
    }
}

enum class LEDChannel { RGB, PLUS_ONE, CH1, CH2, CH3, CH4 }

data class LedActions(
    val onColorSelected: (Int, Int, Int) -> Unit,
    val onToggle: (channel: LEDChannel, enabled: Boolean) -> Unit,
    val onBrightness: (channel: LEDChannel, value: Float) -> Unit,
    val onBrightnessChangeFinished: (channel: LEDChannel) -> Unit
)

val previewLedActions = LedActions(
    onColorSelected = {r,g,b -> Unit},
    onToggle = {channel, enabled -> Unit},
    onBrightness = {channel, value -> Unit},
    onBrightnessChangeFinished = {}
)
