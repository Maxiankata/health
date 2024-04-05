package com.example.healthtracker

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthtracker.data.room.RoomToUserMegaInfoAdapter
import com.example.healthtracker.data.room.UserMegaInfoToRoomAdapter
import com.example.healthtracker.data.user.UserAutomaticInfo
import com.example.healthtracker.data.user.UserMegaInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(private val application: Application):AndroidViewModel(application) {
    private val auth = AuthImpl.getInstance()
    private val userDao = MainActivity.getDatabaseInstance().dao()
    private var fromRoomAdapter= RoomToUserMegaInfoAdapter()
    private var toRoomAdapter = UserMegaInfoToRoomAdapter()

    fun syncCloud(){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                val user = userDao.getEntireUser()
                user?.let {
                    fromRoomAdapter.adapt(it)
                }?.let {
//                    auth.sync(it)

                }
            }
        }
    }
}