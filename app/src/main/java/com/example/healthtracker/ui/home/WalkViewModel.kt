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

    fun resumeSteps() {
        var prevSteps: Int
        Firebase.database.getReference("user/${Firebase.auth.currentUser!!.uid}/userAutomaticInfo/steps/onLeaveSteps").get()
            .addOnCompleteListener { task ->
                prevSteps = task.result.getValue(Int::class.java)!!
                var newSteps: Int
                Firebase.database.getReference("user/${Firebase.auth.currentUser!!.uid}/userAutomaticInfo/steps/onLogSteps").get()
                    .addOnCompleteListener {
                        newSteps = it.result.getValue(Int::class.java)!!
                        val resumedSteps = prevSteps - newSteps
                        _currentSteps.postValue(resumedSteps)
                    }
            }
    }

    fun saveLeaveSteps() {
        val database = Firebase.database.reference
        database.child("user").child(Firebase.auth.currentUser!!.uid).child("userAutomaticInfo").child("steps").child("onLeaveSteps")
            .setValue(previousSteps)
    }

    fun saveCurrentSteps() {
        val database = Firebase.database.reference
        database.child("user").child("userAutomaticInfo").child("steps").child("currentSteps")
            .setValue(currentSteps)
    }

    fun walkingStart(context: Context) {
        walkService = WalkService(context)
    }

}