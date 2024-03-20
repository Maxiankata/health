package com.example.healthtracker.ui.login.register

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
import okhttp3.internal.wait

class RegisterViewModel(private val application: Application) : AndroidViewModel(application) {
    private val auth = AuthImpl.getInstance()
    private val userDao = MainActivity.getDatabaseInstance().dao()
    private var toRoomAdapter= UserMegaInfoToRoomAdapter()
    private var fromRoomAdapter= RoomToUserMegaInfoAdapter()

    suspend fun register(email: String, password: String, name: String){
        auth.createAcc(email, password, name)
        auth.logIn(email,password)
    }
    suspend fun getUser() {
        auth.getEntireUser().collect {
            if (it != null) {
                withContext(Dispatchers.IO) {
                    toRoomAdapter.adapt(it)?.let { it1 ->
                        userDao.saveUser(it1)
                        userDao.getEntireUser()
                            ?.let { it2 -> fromRoomAdapter.adapt(it2) }
                    }
                }
            }
        }
    }
}