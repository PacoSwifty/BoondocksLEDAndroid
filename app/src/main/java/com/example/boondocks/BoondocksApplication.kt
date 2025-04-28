package com.example.boondocks

import android.app.Application
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class BoondocksApplication : Application() {

    override fun onCreate() {
        super.onCreate()

    }
}