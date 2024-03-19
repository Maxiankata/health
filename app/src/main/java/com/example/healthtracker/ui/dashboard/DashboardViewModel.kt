package com.example.healthtracker.ui.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.healthtracker.MainActivity
import com.example.healthtracker.data.user.UserDays
import com.example.healthtracker.data.user.UserMegaInfo
import com.example.healthtracker.data.user.WaterInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Appendable
import java.util.Date

class DashboardViewModel(private val application: Application) : AndroidViewModel(application) {
    val userDao= MainActivity.getDatabaseInstance(application).dao()
    private var _userDay = MutableLiveData<UserDays?>()
    val userDay : LiveData<UserDays?> get() = _userDay
    suspend fun feedDay(date:Date){
        withContext(Dispatchers.IO) {
            val user = userDao.getEntireUser()
            for (item in user?.userDays!!) {
                if (date == item.dateTime) {
                    _userDay.postValue(item)
                }
            }
        }
    }
}