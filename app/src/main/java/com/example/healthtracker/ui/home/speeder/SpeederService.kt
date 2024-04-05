package com.example.healthtracker.ui.home.speeder

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.healthtracker.AuthImpl
import com.example.healthtracker.MainActivity
import com.example.healthtracker.MyApplication
import com.example.healthtracker.R
import com.example.healthtracker.ui.account.friends.challenges.Challenge
import com.example.healthtracker.ui.home.walking.StepCounterService
import com.example.healthtracker.ui.parseDurationToLong
import com.example.healthtracker.ui.stopSpeeder
import com.example.healthtracker.ui.updateStepCalories
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SpeederService : Service() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    companion object {
        private val _speed = MutableLiveData<Double>()
        var timey: String = ""
        val _time = MutableLiveData<String>()
        val time: LiveData<String> get() = _time
        val speed: LiveData<Double> get() = _speed
        var speedIntent = Intent(MyApplication.getContext(), SpeederService::class.java)
        var multiplier: Double = 0.0
    }

    private var divider: Int = 0
    private var divided: Double = 0.0
    private val userDao = MainActivity.getDatabaseInstance().dao()
    private val authImpl = AuthImpl.getInstance()
    private val customCoroutineScope = CoroutineScope(Dispatchers.IO)
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private val NOTIFICATION_ID = 123
    private val CHANNEL_ID = "speeder_channel"
    var weight: Double = 0.0
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        SpeederServiceBoolean.isMyServiceRunning.postValue(true)
        startForeground(NOTIFICATION_ID, createNotification())
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        customCoroutineScope.launch {
            multiplier = if (userDao.getUserSettings()?.units == "kg") {
                3.6
            } else {
                2.24
            }
        }
        val time = speedIntent.getStringExtra("time") ?: "00:00:00"
        timey = time
        customCoroutineScope.launch {
            weight = userDao.getPutInInfo()?.weight!!
        }
        Log.d("challenge extra",speedIntent.getStringExtra("challenge").toString())
        val extra = speedIntent.getStringExtra("challenge").toString()
        val challenge :Challenge? = if (extra =="null"){
            null
        }else {
            Challenge.fromString(speedIntent.getStringExtra("challenge")!!)
        }
        startCountdownFromString(time, onTick = { timeRemaining ->
            _time.postValue(timeRemaining)
            updateNotification()
        }, onFinish = {
            val averageSpeed = divided / divider
            Log.d("weight", weight.toString())
            var MET: Double
            when (ActivityEnum.valueOf(
                speedIntent.getStringExtra("activity") ?: ActivityEnum.WALKING.name
            )) {
                ActivityEnum.RUNNING -> {
                    MET = when (averageSpeed) {
                        in 0.0..10.19 -> 10.0
                        in 10.20..11.39 -> 11.0
                        in 11.4..12.92 -> 12.5
                        in 12.93..15.03 -> 14.0
                        else -> 16.0
                    }
                    val newCals =
                        calculateBurnedCalories(MET, weight, parseDurationToLong(time) / 60000)
                    updateStepCalories(newCals.toInt())
                }

                ActivityEnum.JOGGING -> {
                    MET = when (averageSpeed) {
                        in 0.0..8.84 -> 8.0
                        in 8.85..10.49 -> 10.0
                        in 10.5..12.09 -> 11.5
                        else -> 13.5
                    }
                    val newCals =
                        calculateBurnedCalories(MET, weight, parseDurationToLong(time) / 60000)
                    updateStepCalories(newCals.toInt())
                }

                ActivityEnum.WALKING -> {
                    MET = when (averageSpeed) {
                        in 0.0..4.39 -> 3.0
                        in 4.4..5.19 -> 3.5
                        in 5.2..6.39 -> 4.0
                        else -> 4.5
                    }
                    val newCals =
                        calculateBurnedCalories(MET, weight, parseDurationToLong(time) / 60000)
                    updateStepCalories(newCals.toInt())
                }

                ActivityEnum.CYCLING -> {
                    MET = when (averageSpeed) {
                        in 0.0..19.19 -> 6.0
                        in 19.20..22.39 -> 8.0
                        else -> 10.0
                    }
                    val newCals =
                        calculateBurnedCalories(MET, weight, parseDurationToLong(time) / 60000)
                    updateStepCalories(newCals.toInt())
                }
            }
            customCoroutineScope.launch {
                withContext(Dispatchers.IO) {
                    if (challenge != null) {
                        val user = userDao.getEntireUser()
                        val challenges = user.challenges?.toMutableList()
                        if (challenges != null) {
                            for (item in challenges) {
                                if (item.id == challenge.id) {
                                    item.challengeCompletion = true
                                    Log.d("removing item at", item.id.toString())
                                    Log.d(
                                        "Removed item",
                                        "${item.id} , ${item.assigner}, ${item.challengeType}"
                                    )
                                    challenges.remove(item)
                                    challenges.add(
                                        Challenge(
                                            id = item.id,
                                            image = item.image,
                                            challengeDuration = item.challengeDuration,
                                            assigner = item.assigner,
                                            challengeType = item.challengeType,
                                            challengeCompletion = true
                                        )
                                    )
                                    break
                                }
                            }
                        }
                        user.challenges = challenges
                        user.challenges?.let {
                            userDao.updateChallenges(it)
                            authImpl.setChallenges(it, user.userId)
                        }
                    }
                }
                SpeederServiceBoolean.isMyServiceRunning.postValue(false)
                stopSpeeder()
            }
        })
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                for (location in p0.locations) {
                    divider++
                    divided += location.speed * multiplier
                    _speed.postValue(location.speed * multiplier)
                }
            }
        }

        startLocationUpdates()
    }

    override fun onDestroy() {
        super.onDestroy()
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NOTIFICATION_ID)
        stopLocationUpdates()
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val locationRequest = LocationRequest.Builder(1000).setIntervalMillis(1000)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY).setMaxUpdateDelayMillis(500).build()
        fusedLocationClient.requestLocationUpdates(
            locationRequest, locationCallback, null
        )
    }

    private fun createNotificationChannel() {
        val name = "SpeederChannel"
        val descriptionText = "Speeder Service Notification Channel"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun createNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        return NotificationCompat.Builder(this, StepCounterService.channelId)
            .setContentTitle(speedIntent.getStringExtra("activity")).setContentText(_time.value)
            .setSmallIcon(R.drawable.timer_icon).setContentIntent(pendingIntent).build()
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun updateNotification() {
        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun startCountdownFromString(
        timeString: String, onTick: (String) -> Unit, onFinish: () -> Unit
    ): CountDownTimer {
        val timeParts = timeString.split(":")
        val hours = timeParts[0].toLong()
        val minutes = timeParts[1].toLong()
        val seconds = timeParts[2].toLong()
        val totalTimeInMillis = ((hours * 3600) + (minutes * 60) + seconds) * 1000
        return object : CountDownTimer(totalTimeInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val hoursLeft = millisUntilFinished / (1000 * 60 * 60)
                val minutesLeft = (millisUntilFinished % (1000 * 60 * 60)) / (1000 * 60)
                val secondsLeft = (millisUntilFinished % (1000 * 60)) / 1000
                val formattedTimeLeft =
                    String.format("%02d:%02d:%02d", hoursLeft, minutesLeft, secondsLeft)
                onTick(formattedTimeLeft)
            }

            override fun onFinish() {
                onFinish()
            }
        }.start()
    }

    private fun calculateBurnedCalories(MET: Double, weight: Double, minutes: Long): Double {
        return ((MET * 3.5 * weight / 200) * minutes)
    }
}

