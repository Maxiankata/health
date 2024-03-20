package com.example.healthtracker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.room.Room
import com.example.healthtracker.data.room.UserDB
import com.example.healthtracker.databinding.ActivityMainBinding
import com.example.healthtracker.ui.home.walking.AlarmItem
import com.example.healthtracker.ui.home.walking.AlarmScheduler
import com.example.healthtracker.ui.home.walking.Alarmer
import com.example.healthtracker.ui.home.walking.StepCounterService
import com.example.healthtracker.ui.startStepCounterService
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class MainActivity : AppCompatActivity() {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding
    private val channelID = "friend_channel"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
//        val intent = Intent(this, StepCounterService::class.java)
//        startService(intent)

        startStepCounterService()

        val item = AlarmItem(LocalDateTime.now(),"lols")
        val alarmer = Alarmer()
        alarmer.schedule(item)
        supportActionBar?.hide()
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        lifecycleScope.launch {
            mainViewModel.syncCloud()
        }
        buildNotification()
    }

    private fun buildNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.friends)
            val descriptionText = getString(R.string.new_friend)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            Log.d("NOTIFICATION CHANNEL BUILT", channel.toString())
        }
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

