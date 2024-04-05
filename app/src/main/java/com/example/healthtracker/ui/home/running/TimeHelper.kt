package com.example.healthtracker.ui.home.running

import android.content.Context
import android.content.SharedPreferences
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TimeHelper(context: Context) {

    private var sharedPreferences : SharedPreferences = context.getSharedPreferences(PREFERENCES,Context.MODE_PRIVATE)
    private var dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
    private var timeCounting = false
    private var startTime : Date? = null
    private var stopTime : Date? = null

    init {
        timeCounting = sharedPreferences.getBoolean(COUNTING_KEY, false)

        val startString = sharedPreferences.getString(START_KEY, null)
        if (startString!=null){
            startTime = dateFormat.parse(startString)
        }

        val stopString = sharedPreferences.getString(STOP_KEY, null)
        if (stopString!=null){
            startTime = dateFormat.parse(stopString)
        }
    }
    fun startTime():Date? = startTime
    fun stopTime():Date? = stopTime

    fun setStartTime(date: Date?){
        startTime=date
        with(sharedPreferences.edit()){
            val stringDate = if (date==null) null else dateFormat.format(date)
            putString(START_KEY,stringDate)
            apply()
        }
    }
    fun setStopTime(date: Date?){
        stopTime=date
        with(sharedPreferences.edit()){
            val stringDate = if (date==null) null else dateFormat.format(date)
            putString(STOP_KEY, stringDate)
            apply()
        }
    }
    fun timerCounting(): Boolean = timeCounting
    fun setTimerCounting(boolean:Boolean){
        timeCounting = boolean
        with(sharedPreferences.edit()){
            putBoolean(COUNTING_KEY, boolean)
            apply()
        }
    }
    companion object{
        const val PREFERENCES = "prefs"
        const val START_KEY = "startKey"
        const val STOP_KEY = "stopKey"
        const val COUNTING_KEY = "countingKey"
    }
}