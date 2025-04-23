package com.example.boondocks.ui.lights

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.boondocks.data.Constants
import com.example.boondocks.data.Constants.ANTARCTICA
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

private val _uiState = MutableStateFlow(LightsUiState())

class LightsViewModel : ViewModel() {

    val uiState: StateFlow<LightsUiState> = _uiState.asStateFlow()
    private val _lightsMessageFlow = MutableSharedFlow<String>(replay = 0, extraBufferCapacity = 1)
    val lightsMessageFlow: SharedFlow<String> = _lightsMessageFlow



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

    fun emitJsonMessage(message: String) {
        viewModelScope.launch {
            _lightsMessageFlow.emit(message)
            Log.i(ANTARCTICA, "attempting to emit $message")
        }
    }

}