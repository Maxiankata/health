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
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.healthtracker.AuthImpl
import com.example.healthtracker.MainActivity
import com.example.healthtracker.MyApplication
import com.example.healthtracker.R
import com.example.healthtracker.ui.account.friends.challenges.Challenge
import com.example.healthtracker.ui.account.friends.challenges.ChallengesDisplayDialogViewModel
import com.example.healthtracker.ui.formatDurationFromLong
import com.example.healthtracker.ui.home.walking.StepCounterService
import com.example.healthtracker.ui.isInternetAvailable
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
import kotlin.math.round

class SpeederService : Service() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var timer: CountDownTimer

    companion object {
        private val _speed = MutableLiveData<Double>()
        var timey: String = ""
        private val _time = MutableLiveData<String>()
        val time: LiveData<String> get() = _time
        val speed: LiveData<Double> get() = _speed
        var speedIntent = Intent(MyApplication.getContext(), SpeederService::class.java)
        var multiplier: Double = 0.0
        var activityTime = MutableLiveData<Long>()
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
    private val ENDING_NOTIFICATION_ID = 420
    private val CHANNEL_ID = "speeder_channel"
    var weight: Double = 0.0
    var burnedCals = 0
    var averageSpeeder = 4.20
    var units = ""
    val functionalMultiplier = 3.6
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        SpeederServiceBoolean._isMyServiceRunning.postValue(true)
        startForeground(NOTIFICATION_ID, createNotification())
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        customCoroutineScope.launch {
            multiplier = if (userDao.getUserSettings().units == "kg") {
                units = "kph"
                3.6
            } else {
                units = "mph"
                2.24
            }
        }
        val time = speedIntent.getStringExtra("time") ?: "00:00:00"
        timey = time
        customCoroutineScope.launch {
            weight = if(userDao.getUserSettings().units=="kg") {
                userDao.getPutInInfo()?.weight!!
            }else{
                userDao.getPutInInfo()?.weight!!*0.45
            }
        }
        val challengeExtra = speedIntent.getStringExtra("challenge").toString()
        val challengeId: Int? =
            if (challengeExtra == "null") null else speedIntent.getStringExtra("challenge")?.toInt()
        if (challengeId != null) speedIntent.putExtra("challenge_id", challengeId.toString())
        timer = startCountdownFromString(time, onTick = { timeRemaining ->
            _time.postValue(timeRemaining)
            updateNotification()
        }, onFinish = {
            updateNotification()
            activityTime.postValue(parseDurationToLong(time))
            customCoroutineScope.launch {
                val autoInfo = userDao.getAutomaticInfo()
                autoInfo?.activeTime = activityTime.value?.let { autoInfo?.activeTime?.plus(it) }
                if (autoInfo?.challengesPassed == null) {
                    autoInfo?.challengesPassed = 0
                    autoInfo?.challengesPassed = autoInfo?.challengesPassed?.plus(1)
                } else {
                    autoInfo.challengesPassed = autoInfo.challengesPassed?.plus(1)
                }
                if (autoInfo != null) {
                    userDao.updateUserAutomaticInfo(autoInfo)
                }
            }
            val averageSpeed = divided / divider
            CoroutineScope(Dispatchers.IO).launch{
                averageSpeeder = if (userDao.getUserSettings().units=="kg"){
                    round(averageSpeed * 10) / 10
                }else{
                    round(averageSpeed * 0.62 * 10)/10
                }
            }
            var MET: Double
            when (ActivityEnum.valueOf(
                speedIntent.getStringExtra("activity") ?: ActivityEnum.WALKING.name
            )) {
                ActivityEnum.RUNNING -> {
                    MET = when (averageSpeed) {
                        in 0.0..5.10->5.0
                        in 5.11..10.19 -> 10.0
                        in 10.20..11.39 -> 11.0
                        in 11.4..12.92 -> 12.5
                        in 12.93..15.03 -> 14.0
                        else -> 16.0
                    }
                    val newCals =
                        calculateBurnedCalories(MET, weight, parseDurationToLong(time) / 60000)
                    updateStepCalories(newCals.toInt())
                    burnedCals = newCals.toInt()
                }

                ActivityEnum.JOGGING -> {
                    MET = when (averageSpeed) {
                        in 0.00..4.42-> 4.0
                        in 4.43..8.84 -> 8.0
                        in 8.85..10.49 -> 10.0
                        in 10.5..12.09 -> 11.5
                        else -> 13.5
                    }
                    val newCals =
                        calculateBurnedCalories(MET, weight, parseDurationToLong(time) / 60000)
                    updateStepCalories(newCals.toInt())
                    burnedCals = newCals.toInt()
                }

                ActivityEnum.WALKING -> {
                    MET = when (averageSpeed) {
                        in 0.0..3.19->2.0
                        in 3.2..4.39 -> 3.0
                        in 4.4..5.19 -> 3.5
                        in 5.2..6.39 -> 4.0
                        else -> 4.5
                    }
                    val newCals =
                        calculateBurnedCalories(MET, weight, parseDurationToLong(time) / 60000)
                    updateStepCalories(newCals.toInt())
                    burnedCals = newCals.toInt()
                }

                ActivityEnum.CYCLING -> {
                    MET = when (averageSpeed) {
                        in 0.0..15.99-> 4.0
                        in 16.0..19.19 -> 6.0
                        in 19.20..22.39 -> 8.0
                        in 22.4..25.6->10.0
                        else -> 12.0
                    }
                    val newCals =
                        calculateBurnedCalories(MET, weight, parseDurationToLong(time) / 60000)
                    updateStepCalories(newCals.toInt())
                    burnedCals = newCals.toInt()
                }
            }
            customCoroutineScope.launch {
                withContext(Dispatchers.IO) {
                    if (challengeId != null) {
                        val user = userDao.getEntireUser()
                        val challenges = user.challenges?.toMutableList()
                        if (challenges != null) {
                            for (item in challenges) {
                                if (item.id == challengeId) {
                                    item.challengeCompletion = true
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
                            if (isInternetAvailable(this@SpeederService)) {
                                authImpl.setChallenges(it, user.userId)
                            }
                            ChallengesDisplayDialogViewModel.refeedChallenges()
                        }
                    }
                }
                showEndingNotification()
                SpeederServiceBoolean._isMyServiceRunning.postValue(false)
                stopSpeeder()
            }
        })
        timer.start()
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                for (location in p0.locations) {
                    divider++
                    divided += location.speed * functionalMultiplier
                    _speed.postValue(location.speed * multiplier)
                }
            }
        }
        startLocationUpdates()
    }

    override fun onDestroy() {
        super.onDestroy()
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NOTIFICATION_ID)
        stopLocationUpdates()
        SpeederServiceBoolean._isMyServiceRunning.postValue(false)

        timer.cancel()
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

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun createNotification(): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
        return NotificationCompat.Builder(this, StepCounterService.channelId)
            .setContentTitle(speedIntent.getStringExtra("activity")).setContentText(_time.value)
            .setSmallIcon(R.drawable.timer_icon).setContentIntent(pendingIntent).build()
    }

    private fun createEndingNotification(): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
        val activity = speedIntent.getStringExtra("activity")?.let { ActivityEnum.valueOf(it) }
        val string = when (activity) {
            ActivityEnum.RUNNING -> getString(R.string.ran)
            ActivityEnum.JOGGING -> getString(R.string.jogged)
            ActivityEnum.WALKING -> getString(R.string.walked)
            ActivityEnum.CYCLING -> getString(R.string.cycled)
            null -> "did nothing"
        }
        return NotificationCompat.Builder(this, StepCounterService.channelId)
            .setContentTitle(getString(R.string.completed_activity)).setContentText(
                "${getString(R.string.you)} $string ${getString(R.string.for_word)} $timey ${
                    getString(
                        R.string.with
                    )
                } $averageSpeeder $units ${getString(R.string.and)} ${getString(R.string.burned)} $burnedCals ${
                    getString(
                        R.string.calories_plain
                    )
                }"
            ).setSmallIcon(R.drawable.wizard).setContentIntent(pendingIntent).setAutoCancel(true).build()
    }

    private fun showEndingNotification() {
        val notification = createEndingNotification()
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(ENDING_NOTIFICATION_ID, notification)
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
                val formattedTimeLeft = formatDurationFromLong(millisUntilFinished)
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

