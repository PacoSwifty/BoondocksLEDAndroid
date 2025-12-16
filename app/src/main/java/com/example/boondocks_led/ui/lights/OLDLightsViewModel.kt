package com.example.boondocks_led.ui.lights

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.boondocks_led.data.Constants.TAG
import com.example.boondocks_led.data.LightsRepository
import com.example.boondocks_led.ui.lights.scenePicker.LightsSceneMessage
import com.example.boondocks_led.ui.lights.toggleLights.DriverLightToggleMessage
import com.example.boondocks_led.ui.lights.toggleLights.LightBarToggleMessage
import com.example.boondocks_led.ui.lights.toggleLights.LightList
import com.example.boondocks_led.ui.lights.toggleLights.PassengerLightToggleMessage
import com.example.boondocks_led.ui.lights.toggleLights.WorkLightToggleMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject

private val _uiState = MutableStateFlow(OLDLightsUiState())

@HiltViewModel
class LightsViewModel @Inject constructor(
    private val lightsRepository: LightsRepository
) : ViewModel() {

    val uiState: StateFlow<OLDLightsUiState> = _uiState.asStateFlow()

    //todo: for later reference, here's how to parse a json string into a kotlin data object
    //todo: val jsonString = """{"id":2,"name":"Jane Doe","email_address":"jane.doe@example.com"}"""
    //      val user = Json.decodeFromString<User>(jsonString)
    private fun sceneSelected(scene: Int) {
        val message = LightsSceneMessage(scene)
        val jsonString = Json.encodeToString(message)
        emitJsonMessage(jsonString)
    }

    fun onScene1Clicked() {
        sceneSelected(1)
    }

    fun onScene2Clicked() {
        sceneSelected(2)
    }

    fun onScene3Clicked() {
        sceneSelected(3)
    }

    fun onToggleLightClicked(lightId: LightList) {
        Log.i(TAG, "toggle light clicked: $lightId")
        when (lightId) {
            LightList.FRONT_DRIVER, LightList.BACK_DRIVER -> {
                _uiState.value =
                    _uiState.value.copy(frontDriverEnabled = !_uiState.value.frontDriverEnabled)
                _uiState.value =
                    _uiState.value.copy(backDriveEnabled = !_uiState.value.backDriveEnabled)
                handleToggleJson(LightList.FRONT_DRIVER, _uiState.value.frontDriverEnabled)
            }

            LightList.FRONT_PASSENGER, LightList.BACK_PASSENGER -> {
                _uiState.value =
                    _uiState.value.copy(frontPassengerEnabled = !_uiState.value.frontPassengerEnabled)
                _uiState.value =
                    _uiState.value.copy(backPassengerEnabled = !_uiState.value.backPassengerEnabled)
                handleToggleJson(LightList.FRONT_PASSENGER, _uiState.value.frontPassengerEnabled)

            }

            LightList.REAR_DRIVER, LightList.REAR_PASSENGER -> {
                _uiState.value =
                    _uiState.value.copy(rearDriverEnabled = !_uiState.value.rearDriverEnabled)
                _uiState.value =
                    _uiState.value.copy(rearPassengerEnabled = !_uiState.value.rearPassengerEnabled)
                handleToggleJson(LightList.REAR_DRIVER, _uiState.value.rearDriverEnabled)
            }

            LightList.LIGHT_BAR -> {
                _uiState.value =
                    _uiState.value.copy(lightBarEnabled = !_uiState.value.lightBarEnabled)
                handleToggleJson(LightList.LIGHT_BAR, _uiState.value.lightBarEnabled)
            }
        }
    }

    fun handleToggleJson(lightId: LightList, enabled: Boolean) {
        val valueString = if (enabled) "On" else "Off"

        val message = when (lightId) {
            LightList.FRONT_DRIVER, LightList.BACK_DRIVER -> DriverLightToggleMessage(valueString).toString()
            LightList.FRONT_PASSENGER, LightList.BACK_PASSENGER -> PassengerLightToggleMessage(
                valueString
            ).toString()

            LightList.REAR_DRIVER, LightList.REAR_PASSENGER -> WorkLightToggleMessage(valueString).toString()
            LightList.LIGHT_BAR -> LightBarToggleMessage(valueString).toString()
        }

        val testBoardMessage = "{\"4\": {\"R\":\"255\", \"G\":\"0\", \"B\":\"0\", \"W\":\"0\"}}"
        emitJsonMessage(testBoardMessage)
    }

    /**
     * Scope the co-routine within the ViewModel and then call a suspend function within the repository.
     * The suspend function will use the ViewModelScope.
     */
    private fun emitJsonMessage(message: String) {
        viewModelScope.launch {
//            lightsRepository.emitLightSceneJsonMessage(message)
        }
    }
}