package com.example.healthtracker.ui.login.resetpswd

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.healthtracker.AuthImpl

class ForgotPasswordViewModel(application: Application) :
    AndroidViewModel(application) {
    private val auth = AuthImpl.getInstance()
    suspend fun resetPassword(email: String): Boolean {
        return try {
            auth.resetPassword(email)
        }catch (e:Exception){
            false
        }
    }
}