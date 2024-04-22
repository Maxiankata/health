package com.example.healthtracker

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        applyDarkTheme()
    }

    companion object {
        private lateinit var context: Context
        fun getContext(): Context {
            return context
        }
    }

    private fun applyDarkTheme() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    }
}