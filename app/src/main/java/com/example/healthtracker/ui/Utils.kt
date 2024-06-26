package com.example.healthtracker.ui

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.healthtracker.MyApplication
import com.example.healthtracker.R
import com.example.healthtracker.data.room.UserData
import com.example.healthtracker.data.user.StepsInfo
import com.example.healthtracker.data.user.UserAutomaticInfo
import com.example.healthtracker.data.user.UserDays
import com.example.healthtracker.data.user.UserFriends
import com.example.healthtracker.data.user.UserGoals
import com.example.healthtracker.data.user.UserInfo
import com.example.healthtracker.data.user.UserMegaInfo
import com.example.healthtracker.data.user.UserPutInInfo
import com.example.healthtracker.data.user.UserSettingsInfo
import com.example.healthtracker.data.user.WaterInfo
import com.example.healthtracker.ui.account.friends.challenges.Challenge
import com.example.healthtracker.ui.home.speeder.ActivityEnum
import com.example.healthtracker.ui.home.speeder.SpeederService
import com.example.healthtracker.ui.home.speeder.SpeederServiceBoolean
import com.example.healthtracker.ui.home.walking.StepCounterService
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DataSnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.Duration
import java.util.Calendar

fun navigateToActivity(currentActivity: Activity, targetActivityClass: Class<*>) {
    val intent = Intent(currentActivity, targetActivityClass)
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
    currentActivity.startActivity(intent)
    currentActivity.finish()
}

suspend fun uriToBitmap(contentResolver: ContentResolver, uri: Uri): Bitmap? =
    withContext(Dispatchers.IO) {
        var bitmap: Bitmap? = null
        try {
            val inputStream = contentResolver.openInputStream(uri)
            if (inputStream != null) {
                bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream.close()
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        bitmap
    }


fun bitmapToBase64(bitmap: Bitmap): String {
    val byteArrayOutputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
    val byteArray = byteArrayOutputStream.toByteArray()
    return Base64.encodeToString(byteArray, Base64.DEFAULT)
}

fun base64ToBitmap(base64String: String): Bitmap {
    val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
    return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
}


fun FragmentActivity.hideBottomNav() {
    findViewById<BottomNavigationView>(R.id.nav_view).apply {
        visibility = View.GONE
    }
}

fun FragmentActivity.showBottomNav() {
    findViewById<BottomNavigationView>(R.id.nav_view).apply {
        visibility = View.VISIBLE
    }
}

fun FragmentActivity.showLoading() {
    findViewById<RelativeLayout>(R.id.loadingPanel).apply {
        visibility = View.VISIBLE
    }
}

fun FragmentActivity.hideLoading() {
    findViewById<RelativeLayout>(R.id.loadingPanel).apply {
        visibility = View.GONE
    }
}

fun FragmentActivity.setLoadingVisibility(value: Int) {
    findViewById<RelativeLayout>(R.id.loadingPanel).apply {
        visibility = value
    }
}

fun FragmentActivity.showMainLoading() {
    findViewById<RelativeLayout>(R.id.loadingPanelMain).apply {
        visibility = View.VISIBLE
    }
}

fun FragmentActivity.hideMainLoading() {
    findViewById<RelativeLayout>(R.id.loadingPanelMain).apply {
        visibility = View.GONE
    }
}

fun rotateView(imageView: View, angle: Float) {
    val rotationAnim = ObjectAnimator.ofFloat(imageView, "rotation", angle)
    rotationAnim.duration = 300
    rotationAnim.interpolator = AccelerateDecelerateInterpolator()
    rotationAnim.start()
}

fun UserMegaInfo.toUserData(): UserData {
    return UserData(
        userId = this.userInfo.uid ?: "",
        userInfo = this.userInfo,
        userAutomaticInfo = this.userAutomaticInfo,
        userFriends = this.userFriends,
        userPutInInfo = this.userPutInInfo,
        userSettingsInfo = this.userSettingsInfo,
        userDays = this.userDays,
        challenges = this.challenges,
        achievements = this.achievements
    )
}


fun DataSnapshot.toUserMegaInfo(): UserMegaInfo {
    return UserMegaInfo(
        userInfo = child("userInfo").toUserInfo(),
        userAutomaticInfo = child("userAutomaticInfo").toUserAutomaticInfo(),
        userFriends = child("userFriends").toUserFriendsList(),
        userPutInInfo = child("userPutInInfo").toUserPutInInfo(),
        userSettingsInfo = child("userSettingsInfo").toUserSettingsInfo(),
        userDays = child("userDays").toUserDaysList(),
        challenges = child("challenges").toChallengesList()
    )
}

fun DataSnapshot.toUserInfo(): UserInfo {
    return UserInfo(
        username = child("username").getValue(String::class.java) ?: "",
        uid = child("uid").getValue(String::class.java) ?: "",
        image = child("image").getValue(String::class.java) ?: "",
        mail = child("mail").getValue(String::class.java) ?: "",
        theme = child("theme").getValue(String::class.java) ?: "",
        bgImage = child("bgImage").getValue(String::class.java) ?: "",
        totalSteps = child("totalSteps").getValue(Int::class.java) ?: 0
    )
}

fun DataSnapshot.toUserFriends(): UserFriends {
    return UserFriends(
        uid = child("uid").getValue(String::class.java) ?: "",
        isFriend = child("friend").getValue(Boolean::class.java) ?: true
    )
}

fun DataSnapshot.toUserAutomaticInfo(): UserAutomaticInfo {
    return UserAutomaticInfo(
        steps = child("steps").toStepsInfo(),
        challengesPassed = child("challengesPassed").getValue(Int::class.java),
        activeTime = child("activeTime").getValue(Long::class.java)
    )
}

fun DataSnapshot.toUserPutInInfo(): UserPutInInfo {
    return UserPutInInfo(
        waterInfo = child("waterInfo").toWaterInfo(),
        weight = child("weight").getValue(Double::class.java),
        sleepDuration = child("sleepDuration").getValue(String::class.java),
        units = child("units").getValue(String::class.java)
    )
}

fun DataSnapshot.toWaterInfo(): WaterInfo {
    return WaterInfo(
        currentWater = child("currentWater").getValue(Int::class.java) ?: 0,
        waterCompletion = child("waterCompletion").getValue(Boolean::class.java) ?: false,
        waterGoal = child("waterGoal").getValue(Int::class.java) ?: 6
    )
}

fun DataSnapshot.toUserSettingsInfo(): UserSettingsInfo {
    return UserSettingsInfo(
        language = child("language").getValue(String::class.java) ?: "en",
        units = child("units").getValue(String::class.java) ?: "kg",
        userGoals = child("userGoals").getValue(UserGoals::class.java) ?: UserGoals()
    )
}

fun DataSnapshot.toUserDays(): UserDays {
    return UserDays(
        putInInfo = child("putInInfo").toUserPutInInfo(),
        automaticInfo = child("automaticInfo").toUserAutomaticInfo(),
        dateTime = child("dateTime").getValue(String::class.java)!!
    )
}

fun DataSnapshot.toChallenge(): Challenge {
    return Challenge(
        id = child("id").getValue(Int::class.java)!!,
        assigner = child("assigner").getValue(String::class.java)!!,
        challengeCompletion = child("challengeCompletion").getValue(Boolean::class.java)!!,
        challengeType = child("challengeType").getValue(ActivityEnum::class.java)!!,
        image = child("image").getValue(String::class.java)!!,
        challengeDuration = child("challengeDuration").getValue(String::class.java)!!
    )
}

fun DataSnapshot.toUserGoals(): UserGoals {
    return UserGoals(
        stepGoal = child("stepGoal").getValue(Int::class.java),
        waterGoal = child("waterGoal").getValue(Int::class.java),
        calorieGoal = child("calorieGoal").getValue(Int::class.java),
        sleepGoal = child("sleepGoal").getValue(Double::class.java)
    )
}

fun DataSnapshot.toStepsInfo(): StepsInfo {
    return StepsInfo(
        stepsGoal = child("stepsGoal").getValue(Int::class.java),
        caloriesGoal = child("caloriesGoal").getValue(Int::class.java),
        currentSteps = child("currentSteps").getValue(Int::class.java),
        currentCalories = child("currentCalories").getValue(Int::class.java),
    )
}

fun DataSnapshot.toUserFriendsList(): List<UserFriends> {
    val userFriendsList = mutableListOf<UserFriends>()
    for (childSnapshot in children) {
        userFriendsList.add(childSnapshot.toUserFriends())
    }
    return userFriendsList
}

fun DataSnapshot.toUserDaysList(): List<UserDays> {
    val userDaysList = mutableListOf<UserDays>()
    for (childSnapshot in children) {
        userDaysList.add(childSnapshot.toUserDays())
    }
    return userDaysList
}

fun DataSnapshot.toChallengesList(): List<Challenge> {
    val challengesList = mutableListOf<Challenge>()
    for (childSnapshot in children) {
        challengesList.add(childSnapshot.toChallenge())
    }
    return challengesList
}

fun isInternetAvailable(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val network = connectivityManager.activeNetwork
    val networkCapabilities = connectivityManager.getNetworkCapabilities(network)

    return networkCapabilities != null && (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || networkCapabilities.hasTransport(
        NetworkCapabilities.TRANSPORT_CELLULAR
    ))
}
fun setCalendarTo8pm(){
    StepCounterService.calendar.set(Calendar.MINUTE, 0)
    StepCounterService.calendar.set(Calendar.SECOND, 0)
    StepCounterService.calendar.set(Calendar.HOUR_OF_DAY, 20)
    Log.d("reset time in millis", StepCounterService.calendar.timeInMillis.toString())
}
fun startStepCounterService() {
    ContextCompat.startForegroundService(MyApplication.getContext(), StepCounterService.stepIntent)
}

fun stopStepCounterService() {
    MyApplication.getContext().stopService(StepCounterService.stepIntent)
}

fun startSpeeder(time: String, activity: ActivityEnum, challengeId: Int?) {
    val intent: Intent =
        SpeederService.speedIntent.putExtra("time", time).putExtra("activity", activity.name).putExtra("challenge", challengeId.toString())
    MyApplication.getContext().startForegroundService(intent)
}

fun stopSpeeder() {
    MyApplication.getContext().stopService(SpeederService.speedIntent)
}

fun isServiceRunning(): Boolean {
    return SpeederServiceBoolean._isMyServiceRunning.value!!
}

fun getStepsValue(): Int {
    return StepCounterService._steps.value ?: 0
}

fun nullifyStepCounter() {
    StepCounterService._steps.postValue(0)
    StepCounterService._calories.postValue(0)
}


fun updateStepCalories(calories: Int) {
    StepCounterService._calories.postValue(StepCounterService._calories.value?.plus(calories))
}


fun parseDurationToLong(duration: String): Long {
    val parts = duration.split(":")
    require(parts.size == 3) { "Invalid duration format: $duration" }
    val hours = parts[0].toLong()
    val minutes = parts[1].toLong()
    val seconds = parts[2].toLong()

    return (hours * 3600 + minutes * 60 + seconds) * 1000
}

fun formatDurationFromLong(milliseconds: Long): String {
    val totalSeconds = milliseconds / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}

fun durationToString(duration: Duration): String {
    val hours = duration.toHours()
    val minutes = duration.toMinutes() % 60
    val seconds = duration.seconds % 60
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}

@SuppressLint("SimpleDateFormat")
fun calendarToString(calendar: Calendar): String {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy")
    return dateFormat.format(calendar.time)
}




