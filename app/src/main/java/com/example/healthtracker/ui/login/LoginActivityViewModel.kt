package com.example.healthtracker.ui.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.healthtracker.AuthImpl
import com.example.healthtracker.data.user.UserMegaInfo

class LoginActivityViewModel(private val application: Application): AndroidViewModel(application) {
    val auth = AuthImpl.getInstance()
    suspend fun checkCurrentUser():Boolean{
        return auth.checkCurrentUser()
    }
}