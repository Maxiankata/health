package com.example.healthtracker.ui.home.walking

import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import com.example.healthtracker.R

class WalkService(context: Context) : SensorEventListener {

    private val appContext = context.applicationContext
    private val sensorManager: SensorManager? =
        context.getSystemService(SENSOR_SERVICE) as? SensorManager
    private val stepSensor: Sensor? = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    var currentSteps = MutableLiveData<Int>().apply { value = 0 }
    var caloriesBurned = MutableLiveData<Int>().apply { value = 0 }

    init {
        if (stepSensor == null) {
            showToast(R.string.missing_sensor)
        } else {
            sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onSensorChanged(event: SensorEvent?) {
        currentSteps.value = (currentSteps.value ?: 0) + 1
        caloriesBurned.value = currentSteps.value!! / 25

    }

    fun resetSteps() {
        currentSteps.value = 0
        caloriesBurned.value = 0
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Handle accuracy changes if needed
    }

    private fun showToast(resId: Int) {
        Toast.makeText(appContext, appContext.getString(resId), Toast.LENGTH_SHORT).show()
    }
}
