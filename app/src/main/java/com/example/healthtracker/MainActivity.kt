package com.example.healthtracker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.room.Room
import com.example.healthtracker.data.room.UserDB
import com.example.healthtracker.databinding.ActivityMainBinding
import com.example.healthtracker.ui.home.speeder.SpeederServiceBoolean
import com.example.healthtracker.ui.home.walking.Alarm
import com.example.healthtracker.ui.home.walking.AlarmItem
import com.example.healthtracker.ui.startStepCounterService
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.time.LocalDateTime

class MainActivity : AppCompatActivity() {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding
    private val channelID = "friend_channel"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        mainViewModel.settings.observe(this) {
            mainViewModel.updateLanguage(this)
        }
        mainViewModel.observeDataChanges().observe(this) { dataChanged ->
            if (dataChanged) {
                showNotification()
                mainViewModel.resetNotificationFlag()
            }
        }
        SpeederServiceBoolean._isMyServiceRunning.postValue(false)
        startStepCounterService()
        val item = AlarmItem(LocalDateTime.now(), "wipe it")
        Alarm().schedule(item)
        supportActionBar?.hide()
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    private fun showNotification() {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = channelID
        val notificationId = 1

        val channel =
            NotificationChannel(channelId, "friends", NotificationManager.IMPORTANCE_DEFAULT)
        notificationManager.createNotificationChannel(channel)

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle(getString(R.string.requests))
            .setContentText(getString(R.string.new_friend)).setSmallIcon(R.drawable.friend_add)
            .setAutoCancel(true)

        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    companion object {
        private lateinit var db: UserDB
        fun getDatabaseInstance(): UserDB {
            val context = MyApplication.getContext()
            if (!::db.isInitialized) {
                db = Room.databaseBuilder(
                    context.applicationContext, UserDB::class.java, "user-base"
                ).build()
            }
            return db
        }
    }

}

