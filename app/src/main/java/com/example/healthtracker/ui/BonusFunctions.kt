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
import android.os.Build
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.RelativeLayout
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.healthtracker.MyApplication
import com.example.healthtracker.R
import com.example.healthtracker.data.room.UserData
import com.example.healthtracker.data.user.StepsInfo
import com.example.healthtracker.data.user.UserAutomaticInfo
import com.example.healthtracker.data.user.UserDays
import com.example.healthtracker.data.user.UserGoals
import com.example.healthtracker.data.user.UserInfo
import com.example.healthtracker.data.user.UserMegaInfo
import com.example.healthtracker.data.user.UserPutInInfo
import com.example.healthtracker.data.user.UserSettingsInfo
import com.example.healthtracker.data.user.WaterInfo
import com.example.healthtracker.ui.home.walking.StepCounterService
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DataSnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.joda.time.DateTime
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalDateTime
import java.util.Calendar
import java.util.Date

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
        challenges = this.challenges
    )
}


@RequiresApi(Build.VERSION_CODES.O)
fun DataSnapshot.toUserMegaInfo(): UserMegaInfo {
    return UserMegaInfo(
        userInfo = child("userInfo").toUserInfo(),
        userAutomaticInfo = child("userAutomaticInfo").toUserAutomaticInfo(),
        userFriends = child("userFriends").toUserFriendsList(),
        userPutInInfo = child("userPutInInfo").toUserPutInInfo(),
        userSettingsInfo = child("userSettingsInfo").toUserSettingsInfo(),
        userDays = child("userDays").toUserDaysList()
    )
}

fun DataSnapshot.toUserInfo(): UserInfo {
    return UserInfo(
        username = child("username").getValue(String::class.java) ?: "",
        uid = child("uid").getValue(String::class.java) ?: "",
        image = child("image").getValue(String::class.java) ?: "",
        mail = child("mail").getValue(String::class.java) ?: "",
        theme = child("theme").getValue(String::class.java) ?: "",
        bgImage = child("bgImage").getValue(String::class.java) ?: ""
    )
}

fun DataSnapshot.toUserAutomaticInfo(): UserAutomaticInfo {
    return UserAutomaticInfo(
        steps = child("steps").toStepsInfo(),
        totalSleepHours = child("totalSleepHours").getValue(Double::class.java),
        challengesPassed = child("challengesPassed").getValue(Int::class.java)
    )
}

fun DataSnapshot.toUserPutInInfo(): UserPutInInfo {
    return UserPutInInfo(
        waterInfo = child("waterInfo").toWaterInfo(),
        weight = child("weight").getValue(Double::class.java)
    )
}

fun DataSnapshot.toWaterInfo(): WaterInfo {
    return WaterInfo(
        currentWater = child("currentWater").getValue(Int::class.java) ?: 0,
        waterCompletion = child("waterCompletion").getValue(Boolean::class.java) ?: false
    )
}

fun DataSnapshot.toUserSettingsInfo(): UserSettingsInfo {
    return UserSettingsInfo(
        language = child("language").getValue(String::class.java) ?: "english",
        units = child("units").getValue(String::class.java) ?: "kg",
        userGoals = child("userGoals").getValue(UserGoals::class.java)?: UserGoals()
    )
}

@RequiresApi(Build.VERSION_CODES.O)
fun DataSnapshot.toUserDays(): UserDays {
    return UserDays(
        putInInfo = child("userPutInInfo").toUserPutInInfo(),
        automaticInfo = child("userAutomaticInfo").toUserAutomaticInfo(),
        dateTime = child("dateTime").getValue(String::class.java)!!
    )
}
fun DataSnapshot.toUserGoals(): UserGoals{
    return UserGoals(
        stepGoal = child("stepGoal").getValue(Int::class.java),
        waterGoal = child("waterGoal").getValue(Int::class.java),
        calorieGoal = child("calorieGoal").getValue(Int::class.java),
        sleepGoal = child("sleepGoal").getValue(Double::class.java)
    )
}

fun DataSnapshot.toStepsInfo(): StepsInfo {
    return StepsInfo(
        totalSteps = child("totalSteps").getValue(Int::class.java),
        totalCalories = child("totalCalories").getValue(Int::class.java),
        currentSteps = child("currentSteps").getValue(Int::class.java),
        currentCalories = child("currentCalories").getValue(Int::class.java)
    )
}

fun DataSnapshot.toUserFriendsList(): List<UserInfo> {
    val userFriendsList = mutableListOf<UserInfo>()
    for (childSnapshot in children) {
        userFriendsList.add(childSnapshot.toUserInfo())
    }
    return userFriendsList
}

@RequiresApi(Build.VERSION_CODES.O)
fun DataSnapshot.toUserDaysList(): List<UserDays> {
    val userDaysList = mutableListOf<UserDays>()
    for (childSnapshot in children) {
        userDaysList.add(childSnapshot.toUserDays())
    }
    return userDaysList
}

fun isInternetAvailable(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val network = connectivityManager.activeNetwork
    val networkCapabilities = connectivityManager.getNetworkCapabilities(network)

    return networkCapabilities != null &&
            (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
}
fun startStepCounterService() {
    val serviceIntent = Intent(MyApplication.getContext(), StepCounterService::class.java)
    ContextCompat.startForegroundService(MyApplication.getContext(), serviceIntent)
    Log.d(
        "starting stepper", ContextCompat.startForegroundService(MyApplication.getContext(), serviceIntent)
            .toString()
    )
}
fun durationToString(duration: Duration): String {
    val hours = duration.toHours()
    val minutes = duration.toMinutes() % 60
    val seconds = duration.seconds % 60
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}
fun stringToDuration(str: String): Duration? {
    val parts = str.split(":")
    if (parts.size != 3) {
        return null
    }
    val hours = parts[0].toLongOrNull() ?: return null
    val minutes = parts[1].toLongOrNull() ?: return null
    val seconds = parts[2].toLongOrNull() ?: return null
    return Duration.ofHours(hours).plusMinutes(minutes).plusSeconds(seconds)
}
@SuppressLint("SimpleDateFormat")
fun calendarToString(calendar: Calendar): String {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy")
    return dateFormat.format(calendar.time)
}
@SuppressLint("SimpleDateFormat")
fun stringToCalendar(dateStr: String): Calendar? {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy")
    try {
        val date: Date = dateFormat.parse(dateStr) ?: return null
        val calendar = Calendar.getInstance()
        calendar.time = date
        return calendar
    } catch (e: Exception) {
        // Handle parse exceptions if any
        e.printStackTrace()
    }
    return null
}
