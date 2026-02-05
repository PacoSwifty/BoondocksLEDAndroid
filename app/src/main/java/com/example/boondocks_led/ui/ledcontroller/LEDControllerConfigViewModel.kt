package com.example.boondocks_led.ui.ledcontroller

import androidx.lifecycle.ViewModel
import com.example.boondocks_led.data.ControllerType
import com.example.boondocks_led.data.LEDControllerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class LEDControllerConfigState(
    val selectedControllerIndex: Int = 0,
    val selectedType: ControllerType = ControllerType.RGBW,
    val controllerOptions: List<String> = listOf("Controller 1", "Controller 2", "Controller 3", "Controller 4"),
    val typeOptions: List<ControllerType> = listOf(ControllerType.RGBW, ControllerType.RGBPLUS1, ControllerType.FOURCHANNEL)
)

@HiltViewModel
class LEDControllerConfigViewModel @Inject constructor(
    private val repository: LEDControllerRepository
) : ViewModel() {

    private val _configState = MutableStateFlow(LEDControllerConfigState())
    val configState: StateFlow<LEDControllerConfigState> = _configState.asStateFlow()

    fun initWithController(controllerId: String) {
        val index = (controllerId.toIntOrNull() ?: 1) - 1
        _configState.update { it.copy(selectedControllerIndex = index.coerceIn(0, 3)) }
    }

    fun onControllerSelected(index: Int) {
        _configState.update { it.copy(selectedControllerIndex = index) }
    }

    fun onTypeSelected(type: ControllerType) {
        _configState.update { it.copy(selectedType = type) }
    }

    fun onSave() {
        val state = _configState.value
        val controllerId = (state.selectedControllerIndex + 1).toString()
        val controller = repository.get(controllerId, state.selectedType)
        controller.setIndividualControllerType(state.selectedType)
    }
}
