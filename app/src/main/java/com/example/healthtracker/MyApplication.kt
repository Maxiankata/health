package com.example.healthtracker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class MyApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        applyDarkTheme()
    }

    private fun applyDarkTheme() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    }
}