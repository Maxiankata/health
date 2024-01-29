package com.example.healthtracker.ui.login.register

import androidx.lifecycle.ViewModel
import com.example.healthtracker.AuthImpl

class RegisterViewModel:ViewModel() {
    private val auth = AuthImpl.getInstance()

    suspend fun register(email:String, password:String, name:String){
        auth.createAcc(email,password,name)
    }
}