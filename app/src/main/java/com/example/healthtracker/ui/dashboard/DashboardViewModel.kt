package com.example.healthtracker.ui.dashboard

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.healthtracker.AuthImpl
import com.example.healthtracker.MainActivity
import com.example.healthtracker.data.user.UserDays
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DashboardViewModel(private val application: Application) : AndroidViewModel(application) {
    val userDao = MainActivity.getDatabaseInstance().dao()
    private val authImpl = AuthImpl.getInstance()
    private var _userDay = MutableLiveData<UserDays?>()
    val userDay: LiveData<UserDays?> get() = _userDay
    fun feedDays(feeder: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                async {
                    authImpl.getCurrentUser()?.userDays?.let { userDao.updateDays(it) }
                }.await()
                Log.d("userdays from firebase", authImpl.getCurrentUser()?.userDays.toString())
                Log.d("userdays in feeder", userDao.getEntireUser()?.userDays.toString())
                val user = userDao.getEntireUser()
                for (item in user?.userDays!!) {
                    if (item.dateTime == feeder) {
                        _userDay.postValue(item)
                    }
                }
            }
        }
    }


}