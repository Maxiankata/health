package com.example.healthtracker.ui.home

import android.app.Application
import android.hardware.SensorEventListener
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.healthtracker.AuthImpl
import com.example.healthtracker.MainActivity
import com.example.healthtracker.R
import com.example.healthtracker.data.room.RoomToUserMegaInfoAdapter
import com.example.healthtracker.data.room.UserMegaInfoToRoomAdapter
import com.example.healthtracker.data.user.UserMegaInfo
import com.example.healthtracker.data.user.WaterInfo
import com.example.healthtracker.ui.home.running.RunningService
import com.example.healthtracker.ui.home.walking.WalkService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HomeViewModel(private val application: Application) : AndroidViewModel(application) {
    private val userDao = MainActivity.getDatabaseInstance(application.applicationContext).dao()

    private val _currentService: MutableLiveData<SensorEventListener> = MutableLiveData()
    val currentService: LiveData<SensorEventListener> get() = _currentService


    private val _user = MutableLiveData<UserMegaInfo?>()
    private val _water = MutableLiveData<WaterInfo?>()
    private val roomToUserMegaInfoAdapter = RoomToUserMegaInfoAdapter()
    private val userMegaInfoToRoomAdapter = UserMegaInfoToRoomAdapter()
    private val auth = AuthImpl.getInstance()
    val water: LiveData<WaterInfo?> get() = _water
    val user: LiveData<UserMegaInfo?>
        get() = _user


    suspend fun feedUser() {
        _currentService.postValue(WalkService(application))
        withContext(Dispatchers.IO) {
            val user = userDao.getEntireUser()?.let { roomToUserMegaInfoAdapter.adapt(it) }
            _water.postValue(user?.userPutInInfo?.waterInfo)
            _user.postValue(user)
        }
    }

    suspend fun getUser(): UserMegaInfo? {
        return auth.getCurrentUser()
    }

    suspend fun syncToFireBase(userMegaInfo: UserMegaInfo) {
        auth.sync(userMegaInfo)
    }

    suspend fun syncToDatabase(userMegaInfo: UserMegaInfo) {
        withContext(Dispatchers.IO) {
            val converted = UserMegaInfoToRoomAdapter().adapt(userMegaInfo)
            if (converted != null) {
                converted.userAutomaticInfo?.let { userDao.updateUserAutomaticInfo(it) }

            }
        }
    }

    suspend fun waterIncrement(incrementation: Int) {
        val oldWater = UserMegaInfo.currentUser.value?.userPutInInfo?.waterInfo?.currentWater!!
        if (oldWater == 0 && incrementation == -1) Toast.makeText(
            application, R.string.under_zero, Toast.LENGTH_SHORT
        ).show()
        else if (oldWater >= 0) {
            val newWater = oldWater.plus(
                incrementation
            )
            UserMegaInfo.currentUser.value?.userPutInInfo?.waterInfo?.currentWater = newWater
            withContext(Dispatchers.IO) {
                val daoSender = userMegaInfoToRoomAdapter.adapt(UserMegaInfo.currentUser.value!!)
                daoSender?.userPutInInfo?.let { userDao.updateUserPutInInfo(it) }
            }
            _water.postValue(
                WaterInfo(
                    currentWater = newWater,
                    waterCompletion = _water.value?.waterCompletion,
                    waterGoal = _water.value?.waterGoal
                )
            )
            if (_water.value?.waterGoal == _water.value?.currentWater && _water.value?.waterCompletion == false) {
                _water.postValue(
                    WaterInfo(
                        waterGoal = _water.value?.waterGoal,
                        currentWater = _water.value?.currentWater,
                        waterCompletion = true
                    )
                )
                UserMegaInfo.currentUser.value!!.userPutInInfo?.waterInfo = _water.value
                withContext(Dispatchers.IO) {
                    val userPutInInfo = UserMegaInfo.currentUser.value!!.userPutInInfo
                    if (userPutInInfo != null) {
                        userDao.updateUserPutInInfo(userPutInInfo)
                        Log.d("UserDao water", userDao.getEntireUser()?.userPutInInfo.toString())
                    }
                }
            }
        } else Toast.makeText(application, R.string.under_zero, Toast.LENGTH_SHORT).show()
    }

    suspend fun switchActivity(userActivityStates: UserActivityStates) {
        when (userActivityStates) {
            UserActivityStates.WALKING -> _currentService.value = WalkService(application)
            UserActivityStates.RUNNING -> _currentService.value = RunningService(application)
            UserActivityStates.CYCLING -> Log.d("cycling", "")
            UserActivityStates.HIKING -> Log.d("hiking", "")
            UserActivityStates.POWER_WALKING -> Log.d("power walking", "")
        }

    }
}
