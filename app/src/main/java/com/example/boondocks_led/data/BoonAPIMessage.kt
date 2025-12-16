package com.example.boondocks_led.data

import com.example.boondocks_led.ble.BoonLEDCharacteristic

data class BoonApiMessage(val characteristic: BoonLEDCharacteristic, val json: String)