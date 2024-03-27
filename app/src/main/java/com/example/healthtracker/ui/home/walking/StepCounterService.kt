package com.example.healthtracker.ui.home.walking

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.healthtracker.MainActivity
import com.example.healthtracker.R
import com.example.healthtracker.data.room.RoomToUserMegaInfoAdapter
import com.example.healthtracker.data.user.UserAutomaticInfo
import com.example.healthtracker.data.user.UserMegaInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StepCounterService : Service(), SensorEventListener {
    companion object {
        private val _steps = MutableLiveData<Int>()
        val steps: LiveData<Int> get() = _steps
    }

    private var lastSensorEventTime: Long = 0
    private var isSleeping = false
    private var sleepStartTime: Long = 0
    private var sleepStopTime: Long = 0

    private var sleepDuration: Long = 0

    private val idleThresholdMillis = 2 * 60 * 60 * 1000

    private val channelId = "step_counter_channel"
    private var sensorManager: SensorManager? = null


    private val userDao = MainActivity.getDatabaseInstance().dao()
    private val customCoroutineScope = CoroutineScope(Dispatchers.Main)
    private val roomToUserMegaInfoAdapter = RoomToUserMegaInfoAdapter()
    private fun getUser(): Flow<UserMegaInfo?> = flow {
        val user = withContext(Dispatchers.IO) {
            userDao.getEntireUser()
        }
        emit(user?.let { roomToUserMegaInfoAdapter.adapt(it) })
        Log.d("emitted user", roomToUserMegaInfoAdapter.adapt(user!!).toString())
    }
    private suspend fun getUserSteps() {
        return withContext(Dispatchers.IO) {
            getUser().collect { user ->
                user?.let {
                    Log.d(
                        "steps from dao lul xd",
                        it.userAutomaticInfo?.steps?.currentSteps.toString()
                    )
                    it.userAutomaticInfo?.steps?.currentSteps.let { it1 ->
                        Log.d("current steps from dao in service", it1.toString())
                        _steps.postValue(it1)
                    }
                }?.run {
                    Log.i("null", "user is null for the time being $user")
                }
            }
        }
    }

    private var handler: Handler? = null
    suspend fun updateUserAutomaticInfo() {
        withContext(Dispatchers.IO) {
            val steps = _steps.value
            val user = userDao.getEntireUser()
            user?.userAutomaticInfo.let {
                val renewedAutomaticInfo = UserAutomaticInfo(
                    challengesPassed = it?.challengesPassed ?: 0,
                    totalSleepHours = it?.totalSleepHours,
                    steps = it?.steps?.copy(currentSteps = steps)
                )
                userDao.updateUserAutomaticInfo(renewedAutomaticInfo)
            }
//            Log.d("Running updater", "")
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        customCoroutineScope.launch {
            getUserSteps()
        }
        handler = Handler(Looper.getMainLooper())
        handler?.post(object : Runnable {
            override fun run() {
                customCoroutineScope.launch {
                    updateUserAutomaticInfo()
                    checkForSleep()
                }
                handler?.postDelayed(this, 10000)
            }
        })
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }



    private fun checkForSleep() {
//        Log.d("Checking for sleep", "")
        val currentTime = System.currentTimeMillis()
//        Log.d("calculating", "${currentTime - lastSensorEventTime}")
        if (currentTime - lastSensorEventTime >= idleThresholdMillis && !isSleeping) {
            Log.d("sleep", "im a sleepyhead man, i sleep man, i start sleeping man")
            startSleepTracking()
        }
    }

    private fun startSleepTracking() {
        isSleeping = true
        Log.d("Sleeping start", "")
        sleepStartTime = lastSensorEventTime
    }

    private suspend fun stopSleepTracking() {
        isSleeping = false
        sleepStopTime = System.currentTimeMillis()
        sleepDuration = sleepStopTime - sleepStartTime
        Log.d("Sleep duration", formatDuration(sleepDuration))
//        userDao.updateUserSettings()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(1, createNotification())
        registerStepSensor()
        return START_STICKY
    }

    private fun formatDuration(milliseconds: Long): String {
        val totalSeconds = milliseconds / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60

        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.steps)
            val descriptionText = getString(R.string.steps)
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
        val currentSteps = _steps.value ?: 0
        val builder =
            NotificationCompat.Builder(this, channelId).setSmallIcon(R.drawable.running_icon)
                .setContentTitle(getString(R.string.total_steps))
                .setContentText("$currentSteps Steps").setContentIntent(pendingIntent)
                .setOngoing(true)
                .setSound(null)
        return builder.build()
    }

    private fun registerStepSensor() {
        sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)?.let {
            sensorManager?.registerListener(
                this, it, SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_STEP_COUNTER) {
            _steps.value?.plus(1).let {
                _steps.postValue(it)
                updateNotification()
            }
            Log.d("stepper changed", "stepper gap")
            lastSensorEventTime = System.currentTimeMillis()
            if (isSleeping) {
                customCoroutineScope.launch {
                    stopSleepTracking()
                }
            }
        }
    }

    fun nullifySteps() {
        _steps.postValue(0)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }


    private fun updateNotification() {
        val notification = createNotification()
        startForeground(1, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager?.unregisterListener(this)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}