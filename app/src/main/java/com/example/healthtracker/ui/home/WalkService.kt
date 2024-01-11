package com.example.healthtracker.ui.home

import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class WalkService(context: Context) : SensorEventListener {

//        private var totalSteps = 0f
//    private var previousTotalSteps = 0f
    private var sensorManager: SensorManager? = null
    var currentSteps = MutableLiveData<Int>()
    var caloriesBurned = MutableLiveData<Int>()

    init {
        sensorManager = context.getSystemService(SENSOR_SERVICE) as SensorManager
        val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        if (stepSensor == null) {
            Toast.makeText(context, "No sensor", Toast.LENGTH_SHORT).show()
        } else {
            sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onSensorChanged(event: SensorEvent?) {

        currentSteps.value = (currentSteps.value ?: -1) + 1
        caloriesBurned.value = currentSteps.value!! / 25
        val time = LocalTime.now()
        val resetTime = LocalTime.of(23,59)
        if (time == resetTime){
            Log.d("reset now", "time is $time, corresponding go $resetTime, RESET STEPS NOW")
        }
//        steps can be reset with currentSteps.value = (currentSteps.value ?: -1) + 1
//        TODO("the stepcounter can only be refreshed on device restart,
//              so keep counting until app is exited, then save the steps on leave,
//              subrtact them from the steps on reopen and add current steps")
    }

    fun resetSteps() {
        currentSteps.value = 0
        caloriesBurned.value = 0
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

}