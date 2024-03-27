package com.example.healthtracker.ui.dashboard

import android.app.Application
import android.util.Log
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
import org.joda.time.DateTime
import java.lang.Appendable
import java.time.LocalDateTime
import java.util.Date

class DashboardViewModel(private val application: Application) : AndroidViewModel(application) {
    val userDao= MainActivity.getDatabaseInstance().dao()
    private var _userDay = MutableLiveData<UserDays?>()
    val userDay : LiveData<UserDays?> get() = _userDay
    suspend fun feedDay(string:String){
        withContext(Dispatchers.IO) {
            val user = userDao.getEntireUser()
            for (item in user?.userDays!!) {
                Log.d("Date to search", string)
                Log.d("Date found", item.dateTime)
                if (string == item.dateTime) {
                    _userDay.postValue(item)
                }
            }
        }
    }
}