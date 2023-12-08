package com.example.healthtracker.ui.home

import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.MutableLiveData

class WalkService(context: Context) : SensorEventListener {

//    private var totalSteps = 0f
//    private var previousTotalSteps = 0f
    private var sensorManager: SensorManager?=null
    var currentSteps = MutableLiveData<Int>()
    var caloriesBurned = MutableLiveData<Int>()
    init {
        sensorManager = context.getSystemService(SENSOR_SERVICE) as SensorManager
        val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    if (stepSensor==null){
        Toast.makeText(context,"No sensor",Toast.LENGTH_SHORT).show()
    }else{
        sensorManager?.registerListener(this, stepSensor,SensorManager.SENSOR_DELAY_UI)
    }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        currentSteps.value = (currentSteps.value ?: -1 )+ 1
        caloriesBurned.value = currentSteps.value!! /25
    }
    fun resetSteps(){
        currentSteps.value = 0
        caloriesBurned.value = 0
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

}