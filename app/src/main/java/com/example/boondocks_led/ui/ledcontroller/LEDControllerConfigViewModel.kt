package com.example.boondocks_led.ui.ledcontroller

import androidx.lifecycle.ViewModel
import com.example.boondocks_led.data.ChannelNames
import com.example.boondocks_led.data.ControllerConfig
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
    val typeOptions: List<ControllerType> = listOf(ControllerType.RGBW, ControllerType.RGBPLUS1, ControllerType.FOURCHANNEL),
    val controllerName: String = "",
    val channelName1: String = "",
    val channelName2: String = "",
    val channelName3: String = "",
    val channelName4: String = ""
)

@HiltViewModel
class LEDControllerConfigViewModel @Inject constructor(
    private val repository: LEDControllerRepository
) : ViewModel() {

    private val _configState = MutableStateFlow(LEDControllerConfigState())
    val configState: StateFlow<LEDControllerConfigState> = _configState.asStateFlow()

    fun initWithController(controllerId: String, config: ControllerConfig) {
        val index = (controllerId.toIntOrNull() ?: 1) - 1

        // Read live state from the controller if it exists, so we pick up
        // any name/type changes made since the initial device config was loaded.
        val liveState = repository.get(controllerId, config.type).state.value
        val effectiveConfig = config.copy(
            name = liveState.name,
            type = liveState.type
        )

        _configState.update {
            it.copy(
                selectedControllerIndex = index.coerceIn(0, 3),
                selectedType = effectiveConfig.type,
                controllerName = effectiveConfig.name,
                channelName1 = channelName1FromConfig(effectiveConfig),
                channelName2 = channelName2FromConfig(effectiveConfig),
                channelName3 = channelName3FromConfig(effectiveConfig),
                channelName4 = channelName4FromConfig(effectiveConfig)
            )
        }
    }

    private fun channelName1FromConfig(config: ControllerConfig): String = when (config.type) {
        ControllerType.RGBW -> ""
        ControllerType.RGBPLUS1 -> "${config.channelNames.r} ${config.channelNames.g} ${config.channelNames.b}".trim()
        ControllerType.FOURCHANNEL -> config.channelNames.r
    }

    private fun channelName2FromConfig(config: ControllerConfig): String = when (config.type) {
        ControllerType.RGBW -> ""
        ControllerType.RGBPLUS1 -> config.channelNames.w
        ControllerType.FOURCHANNEL -> config.channelNames.g
    }

    private fun channelName3FromConfig(config: ControllerConfig): String = when (config.type) {
        ControllerType.RGBW, ControllerType.RGBPLUS1 -> ""
        ControllerType.FOURCHANNEL -> config.channelNames.b
    }

    private fun channelName4FromConfig(config: ControllerConfig): String = when (config.type) {
        ControllerType.RGBW, ControllerType.RGBPLUS1 -> ""
        ControllerType.FOURCHANNEL -> config.channelNames.w
    }

    fun onControllerSelected(index: Int) {
        val controllerId = (index + 1).toString()
        val liveState = repository.get(controllerId, ControllerType.RGBW).state.value
        val cn = liveState.channelNames

        _configState.update {
            it.copy(
                selectedControllerIndex = index,
                selectedType = liveState.type,
                controllerName = liveState.name,
                channelName1 = when (liveState.type) {
                    ControllerType.RGBW -> ""
                    ControllerType.RGBPLUS1 -> "${cn.r} ${cn.g} ${cn.b}".trim()
                    ControllerType.FOURCHANNEL -> cn.r
                },
                channelName2 = when (liveState.type) {
                    ControllerType.RGBW -> ""
                    ControllerType.RGBPLUS1 -> cn.w
                    ControllerType.FOURCHANNEL -> cn.g
                },
                channelName3 = when (liveState.type) {
                    ControllerType.RGBW, ControllerType.RGBPLUS1 -> ""
                    ControllerType.FOURCHANNEL -> cn.b
                },
                channelName4 = when (liveState.type) {
                    ControllerType.RGBW, ControllerType.RGBPLUS1 -> ""
                    ControllerType.FOURCHANNEL -> cn.w
                }
            )
        }
    }

    fun onTypeSelected(type: ControllerType) {
        _configState.update {
            it.copy(
                selectedType = type,
                channelName1 = "",
                channelName2 = "",
                channelName3 = "",
                channelName4 = ""
            )
        }
    }

    fun onControllerNameChanged(name: String) {
        _configState.update { it.copy(controllerName = name) }
    }

    fun onChannelNameChanged(index: Int, name: String) {
        _configState.update {
            when (index) {
                1 -> it.copy(channelName1 = name)
                2 -> it.copy(channelName2 = name)
                3 -> it.copy(channelName3 = name)
                4 -> it.copy(channelName4 = name)
                else -> it
            }
        }
    }

    fun onSave() {
        val state = _configState.value
        val controllerId = (state.selectedControllerIndex + 1).toString()
        val controller = repository.get(controllerId, state.selectedType)

        controller.setName(state.controllerName)

        val channelNames = when (state.selectedType) {
            ControllerType.RGBW -> ChannelNames(
                r = state.controllerName,
                g = state.controllerName,
                b = state.controllerName,
                w = state.controllerName
            )
            ControllerType.RGBPLUS1 -> ChannelNames(
                r = state.channelName1,
                g = state.channelName1,
                b = state.channelName1,
                w = state.channelName2
            )
            ControllerType.FOURCHANNEL -> ChannelNames(
                r = state.channelName1,
                g = state.channelName2,
                b = state.channelName3,
                w = state.channelName4
            )
        }

        controller.setIndividualControllerType(state.selectedType, channelNames)
    }
}
