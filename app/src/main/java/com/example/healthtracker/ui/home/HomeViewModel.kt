package com.example.healthtracker.ui.home

import androidx.lifecycle.ViewModel
import com.example.healthtracker.AuthImpl
import com.example.healthtracker.data.user.UserMegaInfo

class HomeViewModel : ViewModel() {


    val auth: AuthImpl = AuthImpl.getInstance()

    suspend fun getUser(): UserMegaInfo? {
        return auth.getEntireUser()
    }


}