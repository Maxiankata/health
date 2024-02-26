package com.example.healthtracker.ui.home

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.healthtracker.AuthImpl
import com.example.healthtracker.MainActivity
import com.example.healthtracker.R
import com.example.healthtracker.data.room.RoomToUserMegaInfoAdapter
import com.example.healthtracker.data.room.UserMegaInfoToRoomAdapter
import com.example.healthtracker.data.user.UserMegaInfo
import com.example.healthtracker.data.user.WaterInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.log

class HomeViewModel(private val application: Application) : AndroidViewModel(application) {
    private val userDao = MainActivity.getDatabaseInstance(application.applicationContext).dao()
    private val _user = MutableLiveData<UserMegaInfo>()
    private val _water = MutableLiveData<WaterInfo?>()
    private val roomToUserMegaInfoAdapter = RoomToUserMegaInfoAdapter()
    private val userMegaInfoToRoomAdapter = UserMegaInfoToRoomAdapter()
    private val auth = AuthImpl.getInstance()
    val water: LiveData<WaterInfo?> get() = _water
    val user: LiveData<UserMegaInfo>
        get() = _user


    suspend fun feedUser() {
        withContext(Dispatchers.IO) {
            val user = userDao.getEntireUser()?.let { roomToUserMegaInfoAdapter.adapt(it) }
            _water.postValue(user?.userPutInInfo?.waterInfo)
            _user.postValue(user!!)
            Log.d("Water in feeder", _water.value.toString())
        }
    }

    suspend fun getUser(): UserMegaInfo? {
        return auth.getCurrentUser()
    }

    suspend fun syncToFireBase(userMegaInfo: UserMegaInfo) {
        auth.sync(userMegaInfo)
    }

    suspend fun waterIncrement(incrementation: Int) {
        val oldWater = UserMegaInfo.currentUser.value?.userPutInInfo?.waterInfo?.currentWater!!
        if (oldWater == 0 && incrementation == -1) Toast.makeText(
            application,
            R.string.under_zero,
            Toast.LENGTH_SHORT
        ).show()
        else if (oldWater >= 0) {
            val newWater = oldWater.plus(
                incrementation
            )
            UserMegaInfo.currentUser.value?.userPutInInfo?.waterInfo?.currentWater = newWater
            withContext(Dispatchers.IO){
                val daoSender = userMegaInfoToRoomAdapter.adapt(UserMegaInfo.currentUser.value!!)
                daoSender?.userPutInInfo?.let { userDao.updateUserPutInInfo(it) }
                Log.d("sent to database", userDao.getPutInInfo().toString())
            }
            _water.postValue(WaterInfo(currentWater = newWater))
        } else Toast.makeText(application, R.string.under_zero, Toast.LENGTH_SHORT).show()
    }
}
