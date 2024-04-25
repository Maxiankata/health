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
import com.example.healthtracker.ui.setCalendarTo8pm
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.Calendar

class StepCounterService : Service(), SensorEventListener {
    companion object {
        var activeService = false
        val _steps = MutableLiveData<Int>()
        val steps: LiveData<Int> get() = _steps
        val _calories = MutableLiveData<Int>()
        val calories: LiveData<Int> get() = _calories
        var _sleepDuration = MutableLiveData<Long>()
        val _activeTime = MutableLiveData<Long>()
        val sleepDuration: LiveData<Long> get() = _sleepDuration
        val channelId = "step_counter_channel"
        var stepIntent: Intent = Intent(MyApplication.getContext(), StepCounterService::class.java)
        val calendar = Calendar.getInstance()
    }

    private var lastSensorEventTime: Long = 0
    private var isSleeping = false
    private var sleepStartTime: Long = 0
    private var sleepStopTime: Long = 0

    private val idleThresholdMillis = 2 * 60 * 60 * 1000

    private var sensorManager: SensorManager? = null
    private val friendChannelID = "friend_channel"
    private val challengeChannelID = "challenge_channel"

    init {
        activeService = true
        setCalendarTo8pm()
    }

    private val userDao = MainActivity.getDatabaseInstance().dao()
    private val roomToUserMegaInfoAdapter = RoomToUserMegaInfoAdapter()
    private fun getUser(callback: (UserMegaInfo?) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            userDao.getEntireUser().let {
                callback(roomToUserMegaInfoAdapter.adapt(it))
                Log.d(
                    "emitted user", roomToUserMegaInfoAdapter.adapt(it).userAutomaticInfo.toString()
                )
            }
        }
    }

    fun getStartingMetrics() {
        getUser { user ->
            user?.let {
                it.userAutomaticInfo?.let { it1 ->
                    CoroutineScope(Dispatchers.Main).launch {
                        _activeTime.postValue(it1.activeTime!!)
                        _calories.value = it1.steps?.currentCalories!!
                        _steps.value = it1.steps.currentSteps!!
                    }
                }
                it.userPutInInfo?.let { it1 ->
                    if (!it1.sleepDuration.isNullOrBlank()) {
                        val sleep = parseDurationToLong(it1.sleepDuration!!)
                        _sleepDuration.postValue(sleep)
                    } else {
                        _sleepDuration.postValue(0)
                    }
                }
            }
        }
    }

    var handler: Handler? = null
    fun updateUserAutomaticInfo() {
        if (!isSleeping){
            checkForSleep()
        }
        CoroutineScope(Dispatchers.IO).launch {
            val user = async { userDao.getEntireUser() }.await()
            user.userAutomaticInfo.let {
                val renewedAutomaticInfo = UserAutomaticInfo(
                    challengesPassed = it?.challengesPassed ?: 0, steps = it?.steps?.copy(
                        currentSteps = _steps.value, currentCalories = _calories.value
                    ), activeTime = it?.activeTime
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
        addListener()
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
        lastSensorEventTime = System.currentTimeMillis()
        if (!isFirstCallback && event.sensor.type == Sensor.TYPE_STEP_COUNTER) {
            _steps.value?.plus(1)?.let {
                _steps.postValue(it)
                if (_steps.value!! % 25 == 0) {
                    _calories.postValue(_calories.value?.plus(1))
                }
                updateNotification()
            }
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
        activeService = false
        pathReference.removeEventListener(friendListener)
        challengeReference.removeEventListener(challengeListener)
        this@StepCounterService.handler?.removeCallbacksAndMessages(null)
        this@StepCounterService.nullifySteps()
        sensorManager?.unregisterListener(this)
        this@StepCounterService.stopSelf()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private val pathReference =
        Firebase.database.reference.child("user").child(Firebase.auth.currentUser!!.uid)
            .child("userFriends")
    private val challengeReference =
        Firebase.database.reference.child("user").child(Firebase.auth.currentUser!!.uid)
            .child("challenges")
    var friendBurnerBoolean = false
    var challengeBurnerBoolean = false
    private val friendListener = object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
             if (friendBurnerBoolean) {
                showFriendNotification()
            }
            friendBurnerBoolean = true
        }

        override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {}

        override fun onChildRemoved(dataSnapshot: DataSnapshot) {}

        override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {}

        override fun onCancelled(databaseError: DatabaseError) {}
    }
    private val challengeListener = object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
            if (challengeBurnerBoolean) {
                showChallengeNotification()
            }
            challengeBurnerBoolean = true
        }

        override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
        }

        override fun onChildRemoved(dataSnapshot: DataSnapshot) {
        }

        override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
        }

        override fun onCancelled(databaseError: DatabaseError) {
        }
    }

    private fun addListener() {
        pathReference.addChildEventListener(friendListener)
        challengeReference.addChildEventListener(challengeListener)
    }

    private fun showFriendNotification() {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = 420
        val channel =
            NotificationChannel(friendChannelID, "friends", NotificationManager.IMPORTANCE_DEFAULT)
        notificationManager.createNotificationChannel(channel)
        val notificationBuilder = NotificationCompat.Builder(this, friendChannelID)
            .setContentTitle(getString(R.string.requests))
            .setContentText(getString(R.string.new_friend)).setSmallIcon(R.drawable.friend_add)
            .setAutoCancel(true)
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    private fun showChallengeNotification() {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = 421
        val channel = NotificationChannel(
            challengeChannelID,
            "challenge",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)
        val notificationBuilder = NotificationCompat.Builder(this, challengeChannelID)
            .setContentTitle(getString(R.string.challenge))
            .setContentText(getString(R.string.new_challenge)).setSmallIcon(R.drawable.flag)
            .setAutoCancel(true)
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

}