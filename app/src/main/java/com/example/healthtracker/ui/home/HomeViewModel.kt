package com.example.healthtracker.ui.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.healthtracker.MainActivity
import com.example.healthtracker.data.user.UserMegaInfo
import com.example.healthtracker.data.user.UserPutInInfo
import com.example.healthtracker.data.user.WaterInfo
import com.example.healthtracker.ui.toUserData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val userDao = MainActivity.getDatabaseInstance(application.applicationContext).dao()
    private val _user = MutableLiveData<UserMegaInfo>()
    private val _water = MutableLiveData<WaterInfo?>()
    val water: LiveData<WaterInfo?>
        get() = _water
    val user: LiveData<UserMegaInfo>
        get() = _user

    init {
        viewModelScope.launch {
             UserMegaInfo.getCurrentUser()?.let {
                _user.postValue(it)
                withContext(Dispatchers.IO) {
                    userDao.saveUser(it.toUserData())
                    it.userInfo.uid?.let { id ->
                        Log.d("USER", "${userDao.getBasicInfo(id)?.uid}")
                    } ?: Log.d("USER", "KOFTI")
                }
                val water = _user.value?.userInfo?.uid?.let { userDao.getPutInInfo(it) }?.waterInfo
                _water.postValue(water)
                Log.d("water info", _water.toString())
            }
        }
    }

    suspend fun waterIncrement(incrementation: Int) {
        _user.let {
            withContext(Dispatchers.IO) {
                val putin = _user.value?.userInfo?.uid?.let { it1 -> userDao.getPutInInfo(it1) }
                Log.d("THIS IS THE USERRRRRR", putin.toString())
            }

            val userPutInInfo = it.value?.userPutInInfo ?: UserPutInInfo()
            val waterInfo = userPutInInfo.waterInfo ?: WaterInfo()
            waterInfo.currentWater = (waterInfo.currentWater ?: 0) + incrementation
            userPutInInfo.waterInfo = waterInfo
            _water.postValue(waterInfo)
            withContext(Dispatchers.IO) {
                user.value?.userInfo?.uid?.let { it1 ->
                    userDao.updateUserWaterInfo(
                        it1, userPutInInfo
                    )
                }
            }
        }
    }
}
