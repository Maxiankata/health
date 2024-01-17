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
import androidx.lifecycle.ViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime


//FIXME this is not a good way to do this
// Define an interface, e.g. StepCountProvider (you also use Repository with a DataSource, since user
// steps are persisted, in that case the DataSource should use SensorManager)
// Then add implementation that uses SensorManager. This provider should have na observable field
// (probably a flow) that updates with the current step count and will also take care of step data
// persistence and management. Then your VMs should only use this provider class and consume the step
// data it provides.
// You might also want to use the process lifecycle, so you can save the last step count available to
// your app when the application is closed. Check https://developer.android.com/jetpack/androidx/releases/lifecycle,
// more specifically  "androidx.lifecycle:lifecycle-process:$lifecycle_version".
// Make sure to use the application context when creating the Provider, you can make it a Singleton
// for simplicity or use dependency injection to provide the instance if you have time to invest in
// DI (https://developer.android.com/training/dependency-injection/hilt-android)
// Another note - the process lifecycle will tell you when the app goes in the background, but that doesn't necessary mean it has been stopped.
// If you want to keep tracking the steps while the app is in the background, you should use a foreground service and make
// sure the user can stop it.
class WalkService(context: Context) : SensorEventListener {

    private var sensorManager: SensorManager? = null
    var currentSteps = MutableLiveData<Int>()
    var caloriesBurned = MutableLiveData<Int>()

    val walkViewModel = WalkViewModel()
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
            walkViewModel.nullifySteps()
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