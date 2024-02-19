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
    private val userDao = MainActivity.getDatabaseInstance(application.applicationContext).dao()
    private var fromRoomAdapter= RoomToUserMegaInfoAdapter()
    private var toRoomAdapter = UserMegaInfoToRoomAdapter()

    suspend fun getUser(){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                val user = userDao.getEntireUser()
                user?.let { fromRoomAdapter.adapt(it) }?.let { UserMegaInfo.setCurrentUser(it) }
            }
        }
    }
    suspend fun syncCloud(){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                val user = userDao.getEntireUser()
                user?.let { fromRoomAdapter.adapt(it) }?.let { auth.sync(it) }
            }
        }
    }
    suspend fun leftWindow(userAutomaticInfo: UserAutomaticInfo){
        withContext(Dispatchers.IO){
            userDao.updateUserAutomaticInfo(userAutomaticInfo)
            syncCloud()
        }
    }
    suspend fun joinedWindow(userAutomaticInfo: UserAutomaticInfo){
        withContext(Dispatchers.IO){
            userDao.updateUserAutomaticInfo(userAutomaticInfo)
        }
    }
}