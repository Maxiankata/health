package com.example.healthtracker.ui.home.speeder

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class SpeederServiceBoolean{
    companion object {
        var _isMyServiceRunning =MutableLiveData<Boolean>()
        val isMyServiceRunning :LiveData<Boolean> get() = _isMyServiceRunning

    }
}