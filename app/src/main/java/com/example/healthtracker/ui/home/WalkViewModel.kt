package com.example.healthtracker.ui.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.healthtracker.AuthImpl
import com.example.healthtracker.data.user.UserInfo
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.database.values
import com.google.firebase.ktx.Firebase

class WalkViewModel : ViewModel() {
    lateinit var walkService: WalkService

    private val _previousSteps = MutableLiveData<Int?>()

    val previousSteps: MutableLiveData<Int?> get() = _previousSteps
    private val _newSteps = MutableLiveData<Int?>()
    val newSteps: MutableLiveData<Int?> get() = _newSteps
    private val _currentSteps = MutableLiveData<Int?>()
    val currentSteps: MutableLiveData<Int?> get() = _currentSteps

    val auth = AuthImpl.getInstance()
    fun nullifySteps() {
        _currentSteps.postValue(0)
    }
    fun walkingStart(context: Context) {
        walkService = WalkService(context)
        _previousSteps.postValue(walkService.currentSteps.value)
    }

}