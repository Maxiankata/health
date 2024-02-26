package com.example.healthtracker.ui.home.walking

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
import androidx.lifecycle.viewModelScope
import com.example.healthtracker.AuthImpl
import com.example.healthtracker.MainActivity
import com.example.healthtracker.R
import com.example.healthtracker.data.room.RoomToUserMegaInfoAdapter
import com.example.healthtracker.data.room.UserMegaInfoToRoomAdapter
import com.example.healthtracker.data.user.StepsInfo
import com.example.healthtracker.data.user.UserAutomaticInfo
import com.example.healthtracker.data.user.UserDays
import com.example.healthtracker.data.user.UserMegaInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

class WalkService(context: Context) : SensorEventListener {

    private val appContext = context.applicationContext
    private val sensorManager: SensorManager? =
        context.getSystemService(SENSOR_SERVICE) as? SensorManager
    private val stepSensor: Sensor? = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    private var _currentSteps = MutableLiveData<Int>().apply { value = 0 }
    val currentSteps : LiveData<Int>  get() = _currentSteps
    private var _caloriesBurned = MutableLiveData<Int>().apply { value = 0 }
    val caloriesBurned : LiveData<Int>  get() = _caloriesBurned
    private val userDao = MainActivity.getDatabaseInstance(context).dao()
    val auth = AuthImpl.getInstance()
    private val customCoroutineScope = CoroutineScope(Dispatchers.Main)
    private val userMegaInfoAdapter = RoomToUserMegaInfoAdapter()

    init {
        if (stepSensor == null) {
            showToast(R.string.missing_sensor)
        } else {
            sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
        }
    }
    suspend fun starter(){
        withContext(Dispatchers.IO){
            val steps = userDao.getAutomaticInfo()
            try {
                if(steps?.steps?.totalSteps!=null){
                    Log.d("STEPS FROM DAO ARE NOT NULL", steps.toString())
                    _currentSteps.postValue(steps.steps.totalSteps)
                }else{
                    Log.d("STEPS FROM DAO ARE NULL", steps.toString())
                }
            }catch (e : Exception){
                Log.e("ERROR", e.toString())
            }
        }
    }
    suspend fun writeSteps(){
        withContext(Dispatchers.IO){
            val steps = _currentSteps.value
            Log.d("CURRENT STEPS", _currentSteps.value.toString())
            UserMegaInfo.currentUser.value?.userAutomaticInfo?.let {
                userDao.updateUserAutomaticInfo(
                    it
                )
            }
            Log.d("USER AUTOMATIC INFO AFTER ADD", userDao.getAutomaticInfo().toString()
            )
        }
    }
    suspend fun nullifySteps() {
        coroutineScope {
            val existingUserDays = userDao.getEntireUser()?.userDays
            val updatedUserDays = existingUserDays?.toMutableList()
            val date = Date()
            val inserter = UserDays(date, userDao.getPutInInfo(), userDao.getAutomaticInfo())
            updatedUserDays?.add(inserter)
            if (updatedUserDays != null) {
                userDao.updateDays(updatedUserDays)
            }
            userDao.wipeUserPutInInfo()
            userDao.wipeUserAutomaticInfo()
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onSensorChanged(event: SensorEvent?) {
        _currentSteps.value = _currentSteps.value?.plus(1)
        UserMegaInfo.currentUser.value?.userAutomaticInfo = UserAutomaticInfo(StepsInfo(totalSteps = _currentSteps.value))
        Log.d("stepping", _currentSteps.value.toString()
        )
        if (_currentSteps.value!! %10==0){
            Log.d("Initiating fireSync", _currentSteps.value.toString())
            customCoroutineScope.launch {

                syncToFirebase()
            }
        }
        _caloriesBurned.value = _currentSteps.value!! / 25

    }
    private suspend fun syncToFirebase(){
        customCoroutineScope.launch {
            val user = UserMegaInfo.getCurrentUser()!!
            user.let { auth.sync(it) }
            Log.d("Firebase sinker", auth.getEntireUser().toString())
        }    }
    fun resetSteps() {
        _currentSteps.value = 0
        _caloriesBurned.value = 0
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    private fun showToast(resId: Int) {
        Toast.makeText(appContext, appContext.getString(resId), Toast.LENGTH_SHORT).show()
    }
}
