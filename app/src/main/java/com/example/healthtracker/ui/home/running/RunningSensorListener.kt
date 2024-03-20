package com.example.healthtracker.ui.home.running

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.util.Log
import androidx.core.app.ActivityCompat
import kotlin.math.sqrt

class RunningSensorListener(private val context: Context) : SensorEventListener, LocationListener {

    private val sensorManager: SensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val locationManager: LocationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager


    private var lastAccelerometerReading = FloatArray(3)
    private var lastAccelerationUpdateTime: Long = 0


    init {
        val accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL)
        if (ActivityCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
//            return
        }
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER, 0, 0f, this
        )
    }
    val gravitationalAcceleration = 9.8f
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastAccelerationUpdateTime > 1000) {// update every second
                val alpha = 0.8f
                lastAccelerometerReading[0] =
                    alpha * lastAccelerometerReading[0] + (1 - alpha) * event.values[0]
                lastAccelerometerReading[1] =
                    alpha * lastAccelerometerReading[1] + (1 - alpha) * event.values[1]
                lastAccelerometerReading[2] =
                    alpha * lastAccelerometerReading[2] + (1 - alpha) * event.values[2]

                val linearAcceleration = calculateLinearAcceleration(
                    event.values[0],
                    event.values[1],
                    event.values[2],
                )
                Log.d("SpeedTracker", "Acceleration: $linearAcceleration")

                lastAccelerationUpdateTime = currentTime
            }
        }
    }

    private fun calculateLinearAcceleration(x: Float, y: Float, z: Float): Float {
        val totalAcceleration = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
        val linearAcceleration = totalAcceleration - gravitationalAcceleration
        return maxOf(0f, linearAcceleration) // Ensure the result is non-negative
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onLocationChanged(location: Location) {
    }
}