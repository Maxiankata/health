//package com.example.healthtracker
//
//import android.app.Service
//import android.content.Intent
//import android.hardware.Sensor
//import android.hardware.SensorEvent
//import android.hardware.SensorEventListener
//import android.hardware.SensorManager
//import android.os.IBinder
//import android.util.Log
//import kotlin.math.log
//
//class StepCount : Service(),SensorEventListener {
//    private lateinit var sensorManager:SensorManager
//    private var stepCount: Int = 0
//    private var totalStepCount = 0f
//
//    override fun onCreate() {
//        super.onCreate()
//        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
//        val stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
//        if (stepSensor==null){
//            Log.d("SENSOR ISSUE", "NO SENSOR FOUND ON THIS DEVICE")
//        }else{
//            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
//            Log.d("SENSOR NOT ISSUE", "SENSOR FOUND ON THIS DEVICE")
//        }
//    }
//
//    override fun onSensorChanged(event: SensorEvent?) {
//        totalStepCount = event!!.values[0]
//        val stepCount = totalStepCount.toInt()
//        Log.d("STEPS", "STEPS COUNTED $stepCount")
//    }
//
//    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
//    }
//
//    override fun onBind(p0: Intent?): IBinder? {
//        TODO("Not yet implemented")
//    }
//
//}