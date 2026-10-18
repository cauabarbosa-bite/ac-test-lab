package com.example.actestlab

import android.app.Application
import com.example.actestlab.data.AppLogger

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        AppLogger.install(this)
    }
}
