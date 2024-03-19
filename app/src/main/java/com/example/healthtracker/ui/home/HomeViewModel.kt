package com.example.healthtracker.ui.home

import android.app.Application
import android.content.Context
import android.hardware.SensorEventListener
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.healthtracker.AuthImpl
import com.example.healthtracker.MainActivity
import com.example.healthtracker.data.room.RoomToUserMegaInfoAdapter
import com.example.healthtracker.data.room.UserMegaInfoToRoomAdapter
import com.example.healthtracker.data.user.UserMegaInfo
import com.example.healthtracker.data.user.UserPutInInfo
import com.example.healthtracker.data.user.UserSettingsInfo
import com.example.healthtracker.data.user.WaterInfo
import com.example.healthtracker.ui.home.running.RunningSensorListener
import com.example.healthtracker.ui.home.walking.StepCounterService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
        withContext(Dispatchers.IO) {
            val user = userDao.getEntireUser()?.let { roomToUserMegaInfoAdapter.adapt(it) }
            _water.postValue(user?.userPutInInfo?.waterInfo)
            _user.postValue(user)
        }
    }

    suspend fun syncMetrics() {
        withContext(Dispatchers.IO) {
            val user = userDao.getEntireUser()
            if (user?.userSettingsInfo?.units == "" || user?.userSettingsInfo?.units == "") {
                val settings = UserSettingsInfo(
                    language = "english", units = "kg"
                )
                userDao.updateUserSettings(settings)
                if (isInternetAvailable(application)) {
                    viewModelScope.launch {
                        auth.sync(roomToUserMegaInfoAdapter.adapt(user))
                    }
                } else {
                    Log.d(
                        "cant sync metrics, no internet",
                        isInternetAvailable(application).toString()
                    )
                }
            }
        }
    }

    suspend fun getUser(): UserMegaInfo? {
        return auth.getCurrentUser()
    }

    suspend fun syncToFireBase(userMegaInfo: UserMegaInfo) {
        auth.sync(userMegaInfo)
    }


    suspend fun waterIncrement(incrementation: Int) {
        withContext(Dispatchers.IO) {
            if (_water.value != null) {
                    val newWater = WaterInfo(
                        waterGoal = _water.value!!.waterGoal,
                        waterCompletion = _water.value!!.waterCompletion,
                        currentWater = _water.value!!.currentWater?.plus(incrementation)
                    )
                    _water.postValue(newWater)
                    val userWeight = userDao.getEntireUser()?.userPutInInfo?.weight
                    val newPutInInfo = UserPutInInfo(waterInfo = newWater, weight = userWeight)
                    userDao.updateUserPutInInfo(newPutInInfo)
                if (_water.value!!.currentWater!! >= _water.value!!.waterGoal!!){
                    val newerWater = WaterInfo(
                        waterGoal = _water.value!!.waterGoal,
                        waterCompletion = true,
                        currentWater = _water.value!!.currentWater
                    )
                    _water.postValue(newerWater)
                    Log.d("Water check", _water.value.toString())
                } else if(_water.value!!.currentWater!! < _water.value!!.waterGoal!!){
                    val newerWater = WaterInfo(
                        waterGoal = _water.value!!.waterGoal,
                        waterCompletion = true,
                        currentWater = _water.value!!.currentWater
                    )
                    _water.postValue(newerWater)
                    Log.d("Water check", _water.value.toString())
                }
            }
        }
    }

    suspend fun switchActivity(userActivityStates: UserActivityStates) {
        when (userActivityStates) {
            UserActivityStates.WALKING -> _currentService.value = StepCounterService()
            UserActivityStates.RUNNING -> _currentService.value = RunningSensorListener(application)
            UserActivityStates.CYCLING -> Log.d("cycling", "")
            UserActivityStates.HIKING -> Log.d("hiking", "")
            UserActivityStates.POWER_WALKING -> Log.d("power walking", "")
        }

    }

    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)

        return networkCapabilities != null && (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || networkCapabilities.hasTransport(
            NetworkCapabilities.TRANSPORT_CELLULAR
        ))
    }
}
