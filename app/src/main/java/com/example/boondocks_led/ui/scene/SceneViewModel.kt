package com.example.boondocks_led.ui.scene

import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class SceneButtonState(
    val text: String = "Scene",
    val isSelected: Boolean = false
)

data class SceneScreenState(
    val buttons: List<SceneButtonState> = List(4) { SceneButtonState() },
    val selectedIndex: Int? = null
)

@HiltViewModel
class SceneViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(SceneScreenState())
    val state: StateFlow<SceneScreenState> = _state.asStateFlow()

    fun onButtonTapped(index: Int) {
        Log.i("SceneViewModel", "Button $index tapped")
        _state.update { currentState ->
            val newButtons = currentState.buttons.mapIndexed { i, button ->
                button.copy(isSelected = i == index)
            }
            currentState.copy(
                buttons = newButtons,
                selectedIndex = index
            )
        }
    }

    fun setButtonText(index: Int, text: String) {
        _state.update { currentState ->
            val newButtons = currentState.buttons.toMutableList()
            if (index in newButtons.indices) {
                newButtons[index] = newButtons[index].copy(text = text)
            }
            currentState.copy(buttons = newButtons)
        }
    }

    fun onSettingsTapped() {
        Log.i("SceneViewModel", "Settings icon tapped")
    }
}
