package com.example.boondocks_led

import android.app.Application
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class BoondocksLEDApplication : Application() {

    override fun onCreate() {
        super.onCreate()

    }
}