package com.example.healthtracker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.room.Room
import com.example.healthtracker.data.room.UserDB
import com.example.healthtracker.data.user.UserMegaInfo
import com.example.healthtracker.databinding.ActivityMainBinding
import com.example.healthtracker.ui.home.walking.WalkViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding
    private val walkViewModel: WalkViewModel by viewModels()
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
//        walkViewModel.setupDailyTask()
        supportActionBar?.hide()
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        lifecycleScope.launch {
            mainViewModel.getUser()
            mainViewModel.syncCloud()
        }
        buildNotification()
    }
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        lifecycleScope.launch {

            if (hasFocus) {
                UserMegaInfo.currentUser.value?.userAutomaticInfo?.let {
                    mainViewModel.joinedWindow(it)
                }

            } else {
                UserMegaInfo.currentUser.value?.userAutomaticInfo?.let {
                    mainViewModel.leftWindow(it)
                }
            }
        }
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
        fun getDatabaseInstance(context: Context): UserDB {
            if (!::db.isInitialized) {
                db = Room.databaseBuilder(
                    context.applicationContext, UserDB::class.java, "user-base"
                ).build()
            }
            return db
        }
    }
}

