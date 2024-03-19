package com.example.healthtracker.ui.login

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.example.healthtracker.AuthImpl
import com.example.healthtracker.MainActivity
import com.example.healthtracker.data.room.RoomToUserMegaInfoAdapter
import com.example.healthtracker.data.room.UserMegaInfoToRoomAdapter
import com.example.healthtracker.data.user.UserMegaInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LoginFragmentViewModel(private val application: Application): AndroidViewModel(application) {
    private val auth = AuthImpl.getInstance()
    private val userDao = MainActivity.getDatabaseInstance(application.applicationContext).dao()
    private val toRoomAdapter = UserMegaInfoToRoomAdapter()
    private val fromRoomAdapter = RoomToUserMegaInfoAdapter()

    suspend fun logIn(email:String,password:String): Boolean {
        return auth.logIn(email,password)
    }
    suspend fun getUser() {
        auth.getEntireUser().collect {
            if (it != null) {
                withContext(Dispatchers.IO) {
                    toRoomAdapter.adapt(it)?.let { it1 ->
                        userDao.saveUser(it1)
                    }
                }
            }
        }
    }
}