package com.example.healthtracker.ui.home.running

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.healthtracker.R
import java.util.Date

class RunningService:Service() {
    val timeHelper = TimeHelper(this)
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action){
            Active.START.toString()-> start()
                Active.STOP.toString()-> stopSelf()

        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun start() {
        val notification = NotificationCompat.Builder(this, "running_channel")
            .setSmallIcon(R.drawable.running_icon)
            .setContentTitle(getString(R.string.running))
            .setContentText(buildString {
                val time = Date().time - restartTime().time
                append(timeStringFromLong(time))
            })
            .build()

        startForeground(1, notification)
    }
    private fun restartTime(): Date {
        val diff = timeHelper.startTime()!!.time - timeHelper.stopTime()!!.time
        return Date(System.currentTimeMillis() + diff)
    }
    private fun timeStringFromLong(time: Long): String {
        val seconds = (time / 1000) % 60
        val minutes = ((time / (1000 * 60)) % 60)
        val hours = ((time / (1000 * 60 * 60)) % 24)
        return makeTimeString(seconds, minutes, hours)

    }

    private fun makeTimeString(seconds: Long, minutes: Long, hours: Long): String {
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
    enum class Active{
        STOP, START
    }
}