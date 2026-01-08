package com.example.boondocks_led

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.boondocks_led.ble.BleManager
import com.example.boondocks_led.ble.ConnectionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val bleManager: BleManager
) : ViewModel() {

    val connectionState: StateFlow<ConnectionState> = bleManager.connectionState

    fun startBle() {
        viewModelScope.launch { bleManager.start() }
    }

    fun stopBle() {
        viewModelScope.launch { bleManager.stop() }
    }
}