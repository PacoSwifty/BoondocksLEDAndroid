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
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LEDControllerViewModel @Inject constructor(
    private val ledControllerRepository: LEDControllerRepository,
) : ViewModel() {

    private var controller: LEDController? = null
    private val _uiState = MutableStateFlow<LEDControllerState?>(null)
    val uiState: StateFlow<LEDControllerState?> = _uiState
    val colorPickerResetEvent: SharedFlow<Unit>? get() = controller?.colorPickerResetEvent

    fun init(controllerId: String, type: ControllerType) {
        if (controller != null) return
        Log.i(TAG, "Calling get from the viewModel")
        controller = ledControllerRepository.get(controllerId, type)
        controller?.setIndividualControllerType(type)

        viewModelScope.launch {
            controller!!.state.collect { _uiState.value = it }
        }
    }

    fun onAllOffClicked() {
        ledControllerRepository.turnOffAll()
    }

    fun onColorSelected(r: Int, g: Int, b: Int) {
        Log.i(TAG, "Selected color R:$r, G:$g, B:$b")

        /** ONLY when we're in RGBW, if the color is mostly white use the white LED, otherwise set a combo of RGB */
        if (controller?.controllerType == ControllerType.RGBW && ((r+g+b)/3 > 240)) {
            Log.i(TAG,"Mostly White color was selected")
            controller?.setRGBColor(0, 0, 0, 255)
        } else {
            controller?.setRGBColor(r, g, b, 0)
        }

    }


    fun onToggleChanged(channel: LEDChannel, enabled: Boolean) {
        val c = controller ?: return

        when (channel) {
            LEDChannel.RGB -> c.setRGBEnabled(enabled)
            LEDChannel.PLUS_ONE -> c.setPlusOneEnabled(enabled)

            LEDChannel.CH1 -> c.setChannelEnabled(index = 1, enabled = enabled)
            LEDChannel.CH2 -> c.setChannelEnabled(index = 2, enabled = enabled)
            LEDChannel.CH3 -> c.setChannelEnabled(index = 3, enabled = enabled)
            LEDChannel.CH4 -> c.setChannelEnabled(index = 4, enabled = enabled)
        }
    }

    /**
     * Any time a user adjusts a slider on any channel we update the state internally. When a user
     * lifts their finger we call the below method onBrightnessFinished()
     */
    fun onBrightnessChanged(channel: LEDChannel, brightness: Float) {
        val c = controller ?: return
        // brightness is only from 0f-1f within the slider, as soon as it leaves that widget (here) we convert to int 0-100
        // and persist it as an int within our state.
        val b = (brightness*100).toInt().coerceIn(0,100)

        when (channel) {
            LEDChannel.RGB -> c.setRGBBrightness(b)
            LEDChannel.PLUS_ONE -> c.setPlusOneBrightness(b)

            LEDChannel.CH1 -> c.setChannelBrightness(index = 1, brightness = b)
            LEDChannel.CH2 -> c.setChannelBrightness(index = 2, brightness = b)
            LEDChannel.CH3 -> c.setChannelBrightness(index = 3, brightness = b)
            LEDChannel.CH4 -> c.setChannelBrightness(index = 4, brightness = b)
        }
    }

    /**
     * This method gets called when the user removes their finger from the slider, at which
     * point we'll commit the value by sending a message to the pico. This is to prevent
     * flooding the communication channel with messages as the user drags the slider.
     */
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
    onColorSelected = { r, g, b -> Unit },
    onToggle = { channel, enabled -> Unit },
    onBrightness = { channel, value -> Unit },
    onBrightnessChangeFinished = {}
)
