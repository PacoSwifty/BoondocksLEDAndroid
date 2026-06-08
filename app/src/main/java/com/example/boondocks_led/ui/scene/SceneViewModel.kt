package com.example.boondocks_led.ui.scene

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.boondocks_led.ble.BleManager
import com.example.boondocks_led.ble.BoonLEDCharacteristic
import com.example.boondocks_led.data.LEDControllerRepository
import com.example.boondocks_led.data.SceneConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.json.Json
import javax.inject.Inject

private const val TAG = "SceneViewModel"
private const val MAX_SCENE_NAME_LENGTH = 10

data class SceneButtonState(
    val text: String = "Scene",
    val isSelected: Boolean = false,
    val isEnabled: Boolean = false
)

data class SceneSelectionState(
    val buttons: List<SceneButtonState> = List(4) { SceneButtonState() },
    val selectedIndex: Int? = null
)

data class SceneConfigurationState(
    val selectedSceneIndex: Int = 0,
    val sceneName: String = "",
    val sceneOptions: List<String> = listOf("Scene 1", "Scene 2", "Scene 3", "Scene 4")
)

@HiltViewModel
class SceneViewModel @Inject constructor(
    private val bleManager: BleManager,
    private val ledControllerRepository: LEDControllerRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SceneSelectionState())
    val state: StateFlow<SceneSelectionState> = _state.asStateFlow()

    private val _configState = MutableStateFlow(SceneConfigurationState())
    val configState: StateFlow<SceneConfigurationState> = _configState.asStateFlow()

    fun applySceneNames(scenes: Map<String, SceneConfig>) {
        _state.update { currentState ->
            val newButtons = currentState.buttons.toMutableList()
            for ((key, scene) in scenes) {
                val index = (key.toIntOrNull() ?: continue) - 1
                if (index in newButtons.indices) {
                    newButtons[index] = newButtons[index].copy(text = scene.name, isEnabled = true)
                }
            }
            currentState.copy(buttons = newButtons)
        }
    }

    fun onButtonTapped(index: Int) {
        Log.i(TAG, "Button $index tapped")
        if (_state.value.buttons.getOrNull(index)?.isEnabled != true) return
        val wasAlreadySelected = _state.value.buttons.getOrNull(index)?.isSelected == true

        if (wasAlreadySelected) {
            // Tapping an already-selected scene acts as "all off"
            deselectAllScenes()
            ledControllerRepository.turnOffAll()
            return
        }

        _state.update { currentState ->
            val newButtons = currentState.buttons.mapIndexed { i, button ->
                button.copy(isSelected = i == index)
            }
            currentState.copy(
                buttons = newButtons,
                selectedIndex = index
            )
        }

        // Send scene select message via BLE
        sendSceneSelectMessage(index)
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

    // Configuration screen methods
    fun onSceneDropdownSelected(index: Int) {
        Log.i(TAG, "Dropdown scene $index selected")
        val currentName = getSceneNameForIndex(index)
        _configState.update { it.copy(selectedSceneIndex = index, sceneName = currentName) }
    }

    private fun getSceneNameForIndex(index: Int): String {
        val buttonText = _state.value.buttons.getOrNull(index)?.text ?: ""
        // Return empty string if it's the default "Scene" text, otherwise return the custom name
        return if (buttonText == "Scene") "" else buttonText
    }

    fun onSceneNameChanged(name: String) {
        val truncatedName = name.take(MAX_SCENE_NAME_LENGTH)
        _configState.update { it.copy(sceneName = truncatedName) }
    }

    fun onSaveSceneTapped() {
        val config = _configState.value
        val index = config.selectedSceneIndex
        val displayName = config.sceneName.ifEmpty { "Scene ${index + 1}" }
        Log.i(TAG, "Save scene tapped - Index: $index, Name: ${config.sceneName}")

        _state.update { currentState ->
            val newButtons = currentState.buttons.toMutableList()
            if (index in newButtons.indices) {
                newButtons[index] = newButtons[index].copy(text = displayName, isEnabled = true)
            }
            currentState.copy(buttons = newButtons)
        }

        sendSceneSaveMessage(index, config.sceneName)
    }

    fun resetConfigurationState() {
        val selectedIndex = _state.value.selectedIndex ?: 0
        val initialName = getSceneNameForIndex(selectedIndex)
        _configState.update { SceneConfigurationState(selectedSceneIndex = selectedIndex, sceneName = initialName) }
    }

    fun onAllOffClicked() {
        deselectAllScenes()
        ledControllerRepository.turnOffAll()
    }

    private fun deselectAllScenes() {
        _state.update { currentState ->
            val newButtons = currentState.buttons.map { it.copy(isSelected = false) }
            currentState.copy(buttons = newButtons, selectedIndex = null)
        }
    }

    // BLE Communication
    private fun sendSceneSelectMessage(sceneIndex: Int) {
        val message = buildSceneSelectMessage(sceneIndex)
        Log.i(TAG, "Sending scene select message: $message")
        bleManager.trySend(BoonLEDCharacteristic.SceneSelect, message)
    }

    private fun sendSceneSaveMessage(sceneIndex: Int, sceneName: String) {
        val message = buildSceneSaveMessage(sceneIndex, sceneName)
        Log.i(TAG, "Sending scene save message: $message")
        bleManager.trySend(BoonLEDCharacteristic.SceneSave, message)
    }

    // Message builders
    // Scene Selection: {"LEDScene": "1"} - value is string "1" through "4"
    private fun buildSceneSelectMessage(sceneIndex: Int): String {
        val sceneNumber = (sceneIndex + 1).toString() // 1-indexed, as string
        val cmd = mapOf("LEDScene" to sceneNumber)
        return Json.encodeToString(cmd)
    }

    // Scene Save: {"1": "CustomName"} - key is scene number as string, value is the name
    private fun buildSceneSaveMessage(sceneIndex: Int, sceneName: String): String {
        val sceneNumber = (sceneIndex + 1).toString() // 1-indexed, as string
        val cmd = mapOf(sceneNumber to sceneName)
        val jsonString = Json.encodeToString(cmd)
        Log.i(TAG, "Scene save message: $jsonString")
        return jsonString
    }
}
