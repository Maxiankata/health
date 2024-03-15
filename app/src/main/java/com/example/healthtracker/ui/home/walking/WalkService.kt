package com.example.healthtracker.ui.home.walking

import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.work.Constraints
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.healthtracker.AuthImpl
import com.example.healthtracker.MainActivity
import com.example.healthtracker.R
import com.example.healthtracker.data.room.RoomToUserMegaInfoAdapter
import com.example.healthtracker.data.user.StepsInfo
import com.example.healthtracker.data.user.UserAutomaticInfo
import com.example.healthtracker.data.user.UserDays
import com.example.healthtracker.data.user.UserMegaInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.Date
import java.util.concurrent.TimeUnit

class WalkService(context: Context) : SensorEventListener {

    private var totalSteps = 0f
    private val appContext = context.applicationContext
    private val sensorManager: SensorManager? = context.getSystemService(SENSOR_SERVICE) as? SensorManager
    private val stepSensor: Sensor? = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    private var _currentSteps = MutableLiveData<Int>().apply { value = 0 }
    val currentSteps: LiveData<Int> get() = _currentSteps
    private var _caloriesBurned = MutableLiveData<Int>().apply { value = 0 }
    val caloriesBurned: LiveData<Int> get() = _caloriesBurned
    private val userDao = MainActivity.getDatabaseInstance(context).dao()
    val auth = AuthImpl.getInstance()
    private val customCoroutineScope = CoroutineScope(Dispatchers.Main)
    private val userMegaInfoAdapter = RoomToUserMegaInfoAdapter()

    init {
        if (stepSensor == null) {
            showToast(R.string.missing_sensor)
        } else {
            sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    suspend fun starter() {
        withContext(Dispatchers.IO) {
            val steps = userDao.getAutomaticInfo()
            try {
                if (steps != null) {
                    Log.d("STEPS FROM DAO ARE NOT NULL", steps.toString())
                    if (steps.steps?.currentSteps != null) _currentSteps.postValue(steps.steps.currentSteps)
                    if (steps.steps?.currentCalories != null) _caloriesBurned.postValue(steps.steps.currentCalories) else {}
                } else {
                    Log.d("STEPS FROM DAO ARE NULL", steps.toString())
                }
            } catch (e: Exception) {
                Log.e("ERROR", e.toString())
            }
        }
    }

    suspend fun writeSteps() {
        withContext(Dispatchers.IO) {
            UserMegaInfo.currentUser.value?.userAutomaticInfo?.let {
                userDao.updateUserAutomaticInfo(
                    it
                )
            }
        }
    }
    suspend fun nullifySteps() {
            withContext(Dispatchers.IO){
                Log.d("Launching wiper", "Launching wiper")
                val existingUserDays = userDao.getEntireUser()?.userDays
                Log.d("ExistingUserDays", existingUserDays.toString())
                val updatedUserDays = existingUserDays?.toMutableList()
                val date = Date()
                Log.d("Date", date.toString()
                )
                val inserter = UserDays(date, userDao.getPutInInfo(), userDao.getAutomaticInfo())
                Log.d("Inserter", inserter.toString())
                updatedUserDays?.add(inserter)
                Log.d("UpdatedUserDays", updatedUserDays.toString())
                if (updatedUserDays != null) {
                    userDao.updateDays(updatedUserDays)
                    Log.d("Updating Dao Days", userDao.getEntireUser()?.userDays.toString())
                }
                userDao.wipeUserPutInInfo()
                userDao.wipeUserAutomaticInfo()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onSensorChanged(event: SensorEvent?) {
        totalSteps = event!!.values[0]
        Log.d("Total Steps", totalSteps.toString())
        _currentSteps.value = _currentSteps.value?.plus(1)
        UserMegaInfo.currentUser.value?.userAutomaticInfo = UserAutomaticInfo(
            StepsInfo(
                currentSteps = _currentSteps.value,
                onLeaveSteps = totalSteps.toInt(),
                currentCalories = _caloriesBurned.value
            )
        )
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()
        val inputData = workDataOf("input" to _currentSteps.value)
        val workRequest = PeriodicWorkRequestBuilder<StepCountWorker>(1, TimeUnit.MINUTES)
            .setInputData(inputData)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(appContext).enqueue(workRequest)
        customCoroutineScope.launch {
            Log.d("User stepped", UserMegaInfo.currentUser.value?.userAutomaticInfo.toString()
            )
            writeSteps()
            withContext(Dispatchers.IO){
                val user = userDao.getEntireUser()
                if (user != null) {
                    Log.d("User auto dao", user.userAutomaticInfo.toString())
                }else{
                    Log.d("kill yourself retarded faggot", "fuck you")
                }
            }

        }
        if (_currentSteps.value!! % 10 == 0) {
            customCoroutineScope.launch {
                syncToFirebase()
            }
        }
        _caloriesBurned.value = _currentSteps.value!! / 25

    }

    private suspend fun syncToFirebase() {
        customCoroutineScope.launch {
            val user = UserMegaInfo.getCurrentUser()!!
            user.let { auth.sync(it) }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    private fun showToast(resId: Int) {
        Toast.makeText(appContext, appContext.getString(resId), Toast.LENGTH_SHORT).show()
    }
}
