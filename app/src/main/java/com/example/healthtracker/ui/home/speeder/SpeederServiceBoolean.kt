package com.example.healthtracker.ui.home.speeder

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class SpeederServiceBoolean{
    companion object {
        var isMyServiceRunning =MutableLiveData<Boolean>()
        val isMyServiceRunningLive :LiveData<Boolean> get() = isMyServiceRunning
    }
}