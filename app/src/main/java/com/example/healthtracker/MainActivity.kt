package com.example.healthtracker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.healthtracker.databinding.ActivityMainBinding
import com.example.healthtracker.ui.home.WalkViewModel
import com.example.healthtracker.ui.login.LoginActivity.Companion.auth
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    val walkViewModel: WalkViewModel by viewModels()
    val channelID = "friend_channel"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        supportActionBar?.hide()
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        auth = FirebaseAuth.getInstance()

        buildNotification()

//        val intent = Intent(this, MainActivity::class.java).apply {
//            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//        }
//        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
//        val builder = NotificationCompat.Builder(this, channelID)
//            .setSmallIcon(R.drawable.ic_launcher_foreground)
//            .setContentTitle("My notification")
//            .setContentText("Hello World!")
//            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//            // Set the intent that fires when the user taps the notification.
//            .setContentIntent(pendingIntent)
//            .setAutoCancel(true)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        //FIXME check comments in WalkService, this will not work for tracking the application lifecycle
        walkViewModel.saveLeaveSteps()
    }

    private fun buildNotification(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name = getString(R.string.friends)
            val descriptionText = getString(R.string.new_friend)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelID, name, importance).apply { description=descriptionText }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            Log.d("NOTIICATION CHANNEL BUILT", channel.toString())
        }
    }
}

