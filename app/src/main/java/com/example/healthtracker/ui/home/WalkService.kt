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

    private var totalSteps = 0f
    private var previousTotalSteps = 0f
    private var sensorManager: SensorManager?=null
    var currentSteps = MutableLiveData<Int>()
    var caloriesBurned = MutableLiveData<Int>()
    init {
        sensorManager = context.getSystemService(SENSOR_SERVICE) as SensorManager


        Log.d("SENSORS", "${sensorManager}")

        currentSteps.value=0
        caloriesBurned.value=0
        val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    if (stepSensor==null){
        Log.d("SENSOR ISSUE", "NO SENSOR FOUND")

        Toast.makeText(context,"No sensor",Toast.LENGTH_SHORT).show()
    }else{
        sensorManager?.registerListener(this, stepSensor,SensorManager.SENSOR_DELAY_UI)
        Log.d("SENSOR ISSUE", "NO SENSOR ISSUE")
        Toast.makeText(context,"Sensor Found",Toast.LENGTH_SHORT).show()

    }
    }

    override fun onSensorChanged(event: SensorEvent?) {
            totalSteps = event!!.values[0]
        currentSteps.value = totalSteps.toInt() - previousTotalSteps.toInt()
        Log.d("STEP CHANGED MAYBE", "WE HAVE STEPPED $currentSteps")
        caloriesBurned.value = currentSteps.value!! /25
    }
    fun resetSteps(){
        currentSteps.value = 0
        previousTotalSteps = totalSteps
        caloriesBurned.value = 0
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

}