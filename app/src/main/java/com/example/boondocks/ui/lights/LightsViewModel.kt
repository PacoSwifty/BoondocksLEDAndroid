package com.example.boondocks.ui.lights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.boondocks.data.LightsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject

private val _uiState = MutableStateFlow(LightsUiState())

@HiltViewModel
class LightsViewModel @Inject constructor(
    private val lightsRepository: LightsRepository
) : ViewModel() {

    val uiState: StateFlow<LightsUiState> = _uiState.asStateFlow()

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

    /**
     * Scope the co-routine within the ViewModel and then call a suspend function within the repository.
     * The suspend function will use the ViewModelScope.
     */
    private fun emitJsonMessage(message: String) {
        viewModelScope.launch {
            lightsRepository.emitLightSceneJsonMessage(message)
        }
    }
}