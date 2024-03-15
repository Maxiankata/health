package com.example.healthtracker.ui.home.walking

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.healthtracker.AuthImpl
import com.example.healthtracker.MainActivity
import com.example.healthtracker.data.room.UserMegaInfoToRoomAdapter
import com.example.healthtracker.data.user.UserDays
import com.example.healthtracker.data.user.UserMegaInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

class WalkViewModel(private val application: Application) : AndroidViewModel(application) {
    var walkService: WalkService = WalkService(application.applicationContext)
    private val _previousSteps = MutableLiveData<Int?>()
    val previousSteps: MutableLiveData<Int?> get() = _previousSteps
    private val _newSteps = MutableLiveData<Int?>()
    val newSteps: MutableLiveData<Int?> get() = _newSteps
    private val _currentSteps = MutableLiveData<Int?>()
    private val userDao = MainActivity.getDatabaseInstance(application).dao()
    val auth = AuthImpl.getInstance()

    suspend fun walkStart(){
        walkService.starter()
    }
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun nullifySteps() {
            withContext(Dispatchers.IO){
            val existingUserDays = userDao.getEntireUser()?.userDays
            val updatedUserDays = existingUserDays?.toMutableList()
            val date = Date()
            val inserter = UserDays(date, userDao.getPutInInfo(), userDao.getAutomaticInfo())
            updatedUserDays?.add(inserter)
            if (updatedUserDays != null) {
                userDao.updateDays(updatedUserDays)
            }
            userDao.wipeUserPutInInfo()
            userDao.wipeUserAutomaticInfo()
        }
    }


    init {
        _previousSteps.postValue(walkService.currentSteps.value)
    }
}