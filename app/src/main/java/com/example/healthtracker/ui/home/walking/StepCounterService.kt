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
import com.example.healthtracker.ui.formatDurationFromLong
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class StepCounterService : Service(), SensorEventListener {
    companion object {
        private val _steps = MutableLiveData<Int>()
        val steps: LiveData<Int> get() = _steps

        private val _calories = MutableLiveData<Int>()
        val calories: LiveData<Int> get() = _calories
        private var _sleepDuration = MutableLiveData<Long>()
        val sleepDuration: LiveData<Long> get() = _sleepDuration

        var stepIntent: Intent? = null
    }

    private var lastSensorEventTime: Long = 0
    private var isSleeping = false
    private var sleepStartTime: Long = 0
    private var sleepStopTime: Long = 0
    val currentTimeMillis = System.currentTimeMillis()
    val calendar = Calendar.getInstance()

    private val idleThresholdMillis = 2 * 60 * 60 * 1000

    private val channelId = "step_counter_channel"
    private var sensorManager: SensorManager? = null

    init {
        _sleepDuration.postValue(0)
        calendar.timeInMillis = currentTimeMillis
        calendar.set(Calendar.MINUTE, 42)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.HOUR_OF_DAY, 8)
    }

    private val userDao = MainActivity.getDatabaseInstance().dao()
    private val customCoroutineScope = CoroutineScope(Dispatchers.Main)
    private val roomToUserMegaInfoAdapter = RoomToUserMegaInfoAdapter()
    private fun getUser(callback: (UserMegaInfo?) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            userDao.getEntireUser()?.let {
                callback(roomToUserMegaInfoAdapter.adapt(it))
                Log.d("emitted user", roomToUserMegaInfoAdapter.adapt(it).userAutomaticInfo.toString())
            } ?: callback(null)
        }
    }

    private fun getUserSteps() {
        getUser { user ->
            user?.let {
                it.userAutomaticInfo?.steps?.currentSteps.let { it1 ->
                    _steps.postValue(it1)
                }
                it.userAutomaticInfo?.steps?.currentCalories.let { it1 ->
                    _calories.postValue(it1)
                }
            }
        }
    }

    var handler: Handler? = null
    suspend fun updateUserAutomaticInfo() {
        withContext(Dispatchers.IO) {
            val user = async { userDao.getEntireUser() }.await()
            user?.userAutomaticInfo.let {
                val renewedAutomaticInfo = UserAutomaticInfo(
                    challengesPassed = it?.challengesPassed ?: 0,
                    totalSleepHours = it?.totalSleepHours,
                    steps = it?.steps?.copy(
                        currentSteps = _steps.value,
                        currentCalories = _calories.value
                    )
                )
                userDao.updateUserAutomaticInfo(renewedAutomaticInfo)
            }
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
        val eightPMMillis = calendar.timeInMillis
        if (System.currentTimeMillis() - lastSensorEventTime >= idleThresholdMillis && !isSleeping && System.currentTimeMillis() >= eightPMMillis) {
            startSleepTracking()
        }
    }

    private fun startSleepTracking() {
        isSleeping = true
        sleepStartTime = lastSensorEventTime
    }

    private fun stopSleepTracking() {
        isSleeping = false
        sleepStopTime = System.currentTimeMillis()
        _sleepDuration.postValue(sleepStopTime - sleepStartTime)
        customCoroutineScope.launch {
            withContext(Dispatchers.IO) {
                val userPutInInfo = async {
                    userDao.getPutInInfo()
                }.await()
                userPutInInfo?.sleepDuration = formatDurationFromLong(_sleepDuration.value!!)
                userPutInInfo?.let {
                    userDao.updateUserPutInInfo(it)
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(1, createNotification())
        registerStepSensor()
        return START_STICKY
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
                .setContentTitle(getString(R.string.today_steps))
                .setContentText("$currentSteps").setContentIntent(pendingIntent)
                .setOngoing(true).setSound(null)
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
                if (it != null) {
                    _calories.postValue(it / 25)
                }
                updateNotification()
            }
            lastSensorEventTime = System.currentTimeMillis()
            if (isSleeping) {
                stopSleepTracking()
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
        this@StepCounterService.handler?.removeCallbacksAndMessages(null)
        this@StepCounterService.nullifySteps()
        sensorManager?.unregisterListener(this)
        this@StepCounterService.stopSelf()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}