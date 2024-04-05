package com.example.healthtracker.ui.account.achievements

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.healthtracker.MainActivity
import com.example.healthtracker.data.room.RoomToUserMegaInfoAdapter
import com.example.healthtracker.data.user.UserMegaInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AchievementsViewModel(private val application: Application) : AndroidViewModel(application) {
    private val _user= MutableLiveData<UserMegaInfo>()
    private val userDao = MainActivity.getDatabaseInstance().dao()
    private val roomToUserMegaInfoAdapter = RoomToUserMegaInfoAdapter()
    val user:LiveData<UserMegaInfo> get() = _user
    fun feedUser(){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                _user.postValue(roomToUserMegaInfoAdapter.adapt(userDao.getEntireUser()))
            }
        }
    }
}