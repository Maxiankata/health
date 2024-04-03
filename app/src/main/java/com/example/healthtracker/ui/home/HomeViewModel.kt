package com.example.healthtracker.ui.home

import android.app.Application
import android.hardware.SensorEventListener
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.healthtracker.AuthImpl
import com.example.healthtracker.MainActivity
import com.example.healthtracker.MyApplication
import com.example.healthtracker.data.room.RoomToUserMegaInfoAdapter
import com.example.healthtracker.data.room.UserMegaInfoToRoomAdapter
import com.example.healthtracker.data.user.UserMegaInfo
import com.example.healthtracker.data.user.UserPutInInfo
import com.example.healthtracker.data.user.WaterInfo
import com.example.healthtracker.ui.calendarToString
import com.example.healthtracker.ui.isInternetAvailable
import com.example.healthtracker.ui.stringToCalendar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.Calendar
import kotlin.math.log

class HomeViewModel(private val application: Application) : AndroidViewModel(application) {
    private val userDao = MainActivity.getDatabaseInstance().dao()

    private val _currentService: MutableLiveData<SensorEventListener> = MutableLiveData()
    val currentService: LiveData<SensorEventListener> get() = _currentService

    private val _user = MutableLiveData<UserMegaInfo?>()
    private val _water = MutableLiveData<WaterInfo?>()
    private val roomToUserMegaInfoAdapter = RoomToUserMegaInfoAdapter()
    private val userMegaInfoToRoomAdapter = UserMegaInfoToRoomAdapter()
    private val auth = AuthImpl.getInstance()
    private val _weight = MutableLiveData<Double?>()
    val weight :LiveData<Double?> get() = _weight
    val water: LiveData<WaterInfo?> get() = _water
    val user: LiveData<UserMegaInfo?>
        get() = _user

    fun getYesterdayWeight(){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                val today = Calendar.getInstance()
                today.add(Calendar.DATE,-1)
                val userDays=userDao.getEntireUser()!!.userDays
                if (userDays!= null) {
                    for (user in userDays){
                        if (user.dateTime== calendarToString(today)){
                            if (user.putInInfo?.weight !=null )
                            _weight.postValue(user.putInInfo.weight)
                        }
                    }
                }
            }
        }
    }
    suspend fun feedUser() {
        withContext(Dispatchers.IO) {
            val user = userDao.getEntireUser()?.let { roomToUserMegaInfoAdapter.adapt(it) }
            _water.postValue(user?.userPutInInfo?.waterInfo)
            runBlocking {
                _user.postValue(user)
            }
        }
    }

    suspend fun checkForChallenges() {
        if (isInternetAvailable(MyApplication.getContext())) {
            withContext(Dispatchers.IO) {
                auth.fetchOwnChallenges()?.let {
                    userDao.updateChallenges(it)
                }
            }
        }
    }

    fun updatePutInInfo(weight: Double) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val userPutIn = async {
                    userDao.getPutInInfo()
                }.await()
                if (userPutIn != null) {
                    userPutIn.weight = weight
                    userDao.updateUserPutInInfo(userPutIn)
                }
            }
        }
    }

//    fun syncToFireBase() {
//        viewModelScope.launch {
//            withContext(Dispatchers.IO) {
//                val user = async {
//                    userDao.getEntireUser()
//                }.await()
//                auth.sync(roomToUserMegaInfoAdapter.adapt(user!!))
//            }
//        }
//    }

    fun waterIncrement(incrementation: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val goal = _user.value?.userSettingsInfo?.userGoals?.waterGoal
                goal?.let {
                    if (_water.value != null) {
                        if (!(water.value!!.currentWater == 0 && incrementation == -1))
                            if (_water.value!!.currentWater!!.plus(incrementation) >= goal) {
                                val newerWater = WaterInfo(
                                    waterCompletion = true,
                                    currentWater = _water.value!!.currentWater?.plus(incrementation)
                                )
                                _water.postValue(newerWater)
                                val userWeight = userDao.getEntireUser()?.userPutInInfo?.weight
                                val newPutInInfo =
                                    UserPutInInfo(waterInfo = newerWater, weight = userWeight)
                                userDao.updateUserPutInInfo(newPutInInfo)
                                Log.d("Water check", _water.value.toString())

                            } else if (_water.value!!.currentWater!!.plus(incrementation) < goal) {
                                val newerWater = WaterInfo(
                                    waterCompletion = false,
                                    currentWater = _water.value!!.currentWater?.plus(incrementation)
                                )
                                _water.postValue(newerWater)
                                val userWeight = userDao.getEntireUser()?.userPutInInfo?.weight
                                val newPutInInfo =
                                    UserPutInInfo(waterInfo = newerWater, weight = userWeight)
                                userDao.updateUserPutInInfo(newPutInInfo)
                                Log.d("Water check", _water.value.toString())

                            }
                    }
                }
            }
        }
    }

}
