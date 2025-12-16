package com.example.boondocks_led

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.boondocks_led.ble.BleManager
import com.example.boondocks_led.ble.BoonLEDCharacteristic
import com.example.boondocks_led.data.BoonApiMessage
import com.example.boondocks_led.data.LightsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    lightsRepository: LightsRepository,
    private val bleManager: BleManager
) : ViewModel() {
    val lightsMessageFlow = lightsRepository.lightsMessageFlow

    fun startBle() {
        viewModelScope.launch { bleManager.start() }
    }

    fun stopBle() {
        viewModelScope.launch { bleManager.stop() }
    }

    fun sendJson(apiMessage: BoonApiMessage) {
        val characteristic = apiMessage.characteristic
        val json = apiMessage.json
        viewModelScope.launch { bleManager.send(characteristic, json) }
    }

}