package com.example.healthtracker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.healthtracker.data.user.UserMegaInfo

class MainViewModel(application: Application):AndroidViewModel(application) {
    private val auth = AuthImpl.getInstance()

    suspend fun getUser(){
        auth.getEntireUser().collect {
            if (it != null) {
                UserMegaInfo.setCurrentUser(it)
            }
        }
    }
}