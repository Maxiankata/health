package com.example.healthtracker.ui.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.healthtracker.MainActivity
import com.example.healthtracker.data.user.UserDays
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DashboardViewModel(application: Application) : AndroidViewModel(application) {
    val userDao = MainActivity.getDatabaseInstance().dao()
    private var _userDay = MutableLiveData<UserDays?>()
    val userDay: LiveData<UserDays?> get() = _userDay
    var units: String? = ""
    fun feedDays(feeder: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val user = userDao.getEntireUser()
                for (item in user.userDays!!) {
                    if (item.dateTime == feeder) {
                        _userDay.postValue(item)
                        break
                    }
                }
            }
        }
    }

    fun getUserUnits() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                units = userDao.getUserSettings().units
            }
        }
    }


}