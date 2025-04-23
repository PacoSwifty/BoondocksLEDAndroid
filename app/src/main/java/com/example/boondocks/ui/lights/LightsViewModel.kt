package com.example.boondocks.ui.lights

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.boondocks.data.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

private val _uiState = MutableStateFlow(LightsUiState())

class LightsViewModel : ViewModel() {

    val uiState: StateFlow<LightsUiState> = _uiState.asStateFlow()

    fun sceneSelected(scene: Int) {
        Log.i(Constants.ANTARCTICA, "Selected Scene $scene.")
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

}