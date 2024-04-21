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
import com.example.healthtracker.MyApplication
import com.example.healthtracker.R
import com.example.healthtracker.data.room.RoomToUserMegaInfoAdapter
import com.example.healthtracker.data.user.UserAutomaticInfo
import com.example.healthtracker.data.user.UserMegaInfo
import com.example.healthtracker.ui.formatDurationFromLong
import com.example.healthtracker.ui.parseDurationToLong
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.Calendar

class StepCounterService : Service(), SensorEventListener {
    companion object {
        val _steps = MutableLiveData<Int>()
        val steps: LiveData<Int> get() = _steps
        val _calories = MutableLiveData<Int>()
        val calories: LiveData<Int> get() = _calories
        var _sleepDuration = MutableLiveData<Long>()
        val _activeTime = MutableLiveData<Long>()
        val sleepDuration: LiveData<Long> get() = _sleepDuration
        val channelId = "step_counter_channel"

        var stepIntent: Intent = Intent(MyApplication.getContext(), StepCounterService::class.java)
    }

    private var lastSensorEventTime: Long = 0
    private var isSleeping = false
    private var sleepStartTime: Long = 0
    private var sleepStopTime: Long = 0
    private val currentTimeMillis = System.currentTimeMillis()
    private val calendar = Calendar.getInstance()

    private val idleThresholdMillis = 2 * 60 * 60 * 1000

    private var sensorManager: SensorManager? = null

    init {
        calendar.timeInMillis = currentTimeMillis
        calendar.set(Calendar.MINUTE, 42)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.HOUR_OF_DAY, 8)
    }

    private val userDao = MainActivity.getDatabaseInstance().dao()
    private val roomToUserMegaInfoAdapter = RoomToUserMegaInfoAdapter()
    private fun getUser(callback: (UserMegaInfo?) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            userDao.getEntireUser().let {
                callback(roomToUserMegaInfoAdapter.adapt(it))
                Log.d(
                    "emitted user",
                    roomToUserMegaInfoAdapter.adapt(it).userAutomaticInfo.toString()
                )
            }
        }
    }

    fun getStartingMetrics() {
        getUser { user ->
            user?.let {
                it.userAutomaticInfo?.let {it1->
                    Log.d("captured pasting mechanism time", it1.toString())
                    CoroutineScope(Dispatchers.Main).launch{
                        _activeTime.postValue(it1.activeTime!!)
                        Log.d("steps info from getter", it1.steps.toString())
                        _calories.value = it1.steps?.currentCalories!!
                        Log.d("UPDATED CALORIES", _calories.value.toString())//DO NOT DELETE DO NOT DO NOT
                        _steps.value = it1.steps.currentSteps!!
                        Log.d("UPDATED STEPS", _steps.value.toString())//DO NOT DELETE DO NOT DO NOT
                        if (!it.userPutInInfo?.sleepDuration.isNullOrBlank()) {
                            val sleep = parseDurationToLong(it.userPutInInfo?.sleepDuration!!)
                            _sleepDuration.postValue(sleep)
                        } else {
                            _sleepDuration.postValue(0)
                        }
                    }

                }
            }
        }
    }

    var handler: Handler? = null
    fun updateUserAutomaticInfo() {
        CoroutineScope(Dispatchers.IO).launch {
            val user = async { userDao.getEntireUser() }.await()
            user.userAutomaticInfo.let {
                val renewedAutomaticInfo = UserAutomaticInfo(
                    challengesPassed = it?.challengesPassed ?: 0,
                    steps = it?.steps?.copy(
                        currentSteps = _steps.value, currentCalories = _calories.value
                    ),
                    activeTime = it?.activeTime
                )
                userDao.updateUserAutomaticInfo(renewedAutomaticInfo)


            }
        }
    }


    override fun onCreate() {
        super.onCreate()
        getStartingMetrics()
        createNotificationChannel()
        handler = Handler(Looper.getMainLooper())
        handler?.post(object : Runnable {
            override fun run() {
                updateUserAutomaticInfo()
                checkForSleep()
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
        CoroutineScope(Dispatchers.IO).launch {
            val userPutInInfo = async {
                userDao.getPutInInfo()
            }.await()
            userPutInInfo?.sleepDuration = formatDurationFromLong(_sleepDuration.value!!)
            userPutInInfo?.let {
                userDao.updateUserPutInInfo(it)
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
                .setContentTitle(getString(R.string.today_steps)).setContentText("$currentSteps")
                .setContentIntent(pendingIntent).setOngoing(true).setSound(null)
        return builder.build()
    }

    private fun registerStepSensor() {
        sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)?.let {
            sensorManager?.registerListener(
                this, it, SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }
    private var isFirstCallback = true

    override fun onSensorChanged(event: SensorEvent) {

        if (!isFirstCallback && event.sensor.type == Sensor.TYPE_STEP_COUNTER) {
            _steps.value?.plus(1)?.let {
                _steps.postValue(it)
                if (_steps.value!! % 25 == 0) {
                    _calories.postValue(_calories.value?.plus(1))
                }
                updateNotification()
            }
            lastSensorEventTime = System.currentTimeMillis()
            if (isSleeping) {
                stopSleepTracking()
            }
        }
        isFirstCallback = false
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