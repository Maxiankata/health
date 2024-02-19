package com.example.healthtracker.ui.home.walking

import android.app.Application
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.healthtracker.AuthImpl
import com.example.healthtracker.MainActivity
import com.example.healthtracker.data.user.UserDays
import kotlinx.coroutines.launch
import androidx.work.Constraints
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class WalkViewModel(private val application: Application) : AndroidViewModel(application) {
    lateinit var walkService: WalkService

    private val _previousSteps = MutableLiveData<Int?>()

    val previousSteps: MutableLiveData<Int?> get() = _previousSteps
    private val _newSteps = MutableLiveData<Int?>()
    val newSteps: MutableLiveData<Int?> get() = _newSteps
    private val _currentSteps = MutableLiveData<Int?>()
    val userDao = MainActivity.getDatabaseInstance(application).dao()
    val auth = AuthImpl.getInstance()

    @RequiresApi(Build.VERSION_CODES.O)
     fun nullifySteps() {
        viewModelScope.launch {

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

    fun walkingStart(context: Context) {
        walkService = WalkService(context)
        _previousSteps.postValue(walkService.currentSteps.value)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun isMidnight(): Boolean {
        val time = LocalTime.now()
        val resetTime = LocalTime.of(0, 0, 0)
        return time == resetTime
    }

    fun setupDailyTask() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .setRequiresCharging(false)
            .setRequiresBatteryNotLow(true)
            .setRequiresDeviceIdle(false)
            .build()

        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val currentTime = dateFormat.format(Calendar.getInstance().time)

        val request = PeriodicWorkRequestBuilder<WalkResetTask>(
            repeatInterval = 24, // Repeat every 24 hours
            repeatIntervalTimeUnit = TimeUnit.HOURS,
            flexTimeInterval = 1, // Flex interval to provide flexibility for WorkManager
            flexTimeIntervalUnit = TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .setInitialDelay(calculateInitialDelay(currentTime), TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(getApplication()).enqueueUniquePeriodicWork(
            "dailyTask",
            ExistingPeriodicWorkPolicy.REPLACE,
            request
        )
    }
    private fun calculateInitialDelay(currentTime: String): Long {
        val targetTime = "00:00"
        val targetCalendar = Calendar.getInstance().apply {
            time = SimpleDateFormat("HH:mm", Locale.getDefault()).parse(targetTime)!!
        }

        val currentCalendar = Calendar.getInstance().apply {
            time = SimpleDateFormat("HH:mm", Locale.getDefault()).parse(currentTime)!!
        }

        if (targetCalendar.before(currentCalendar) || targetCalendar == currentCalendar) {
            targetCalendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return targetCalendar.timeInMillis - currentCalendar.timeInMillis
    }


}