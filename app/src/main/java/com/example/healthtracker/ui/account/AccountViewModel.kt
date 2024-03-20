package com.example.healthtracker.ui.account

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.healthtracker.AuthImpl
import com.example.healthtracker.MainActivity
import com.example.healthtracker.R
import com.example.healthtracker.data.room.RoomToUserMegaInfoAdapter
import com.example.healthtracker.data.user.UserInfo
import com.example.healthtracker.data.user.UserMegaInfo
import com.example.healthtracker.ui.bitmapToBase64
import com.example.healthtracker.ui.home.walking.StepCounterService
import com.example.healthtracker.ui.isInternetAvailable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.prefs.Preferences
import kotlin.coroutines.coroutineContext

class AccountViewModel(private val application: Application) : AndroidViewModel(application) {
    private val auth = AuthImpl.getInstance()
    private val userDao = MainActivity.getDatabaseInstance().dao()
    private val roomToUserMegaInfoAdapter = RoomToUserMegaInfoAdapter()
    suspend fun signOut() {
        val stepCounterService = StepCounterService()
        viewModelScope.launch {
            auth.signOut()
            withContext(Dispatchers.IO){
                userDao.dropUser()
//                stepCounterService.stopSelf()
            }
        }
    }
    suspend fun saveBitmapToDatabase(bitmap: Bitmap) {
        withContext(Dispatchers.IO) {
            userDao.getEntireUser()?.let { roomToUserMegaInfoAdapter.adapt(it) }?.userInfo?.let {
                val newImageInfo =
                    UserInfo(it.username, it.uid, bitmapToBase64(bitmap), it.mail, it.theme, it.bgImage)
                it.let {
                    userDao.updateImage(
                        newImageInfo
                    )
                }
                val image = bitmapToBase64(bitmap)
                Log.d("SAVED IMAGE TO LOCAL", image)
                if (isInternetAvailable(application.applicationContext)) {
                    auth.saveBitmapToDatabase(bitmap)
                    Log.d("SAVED IMAGE TO NETWORK", image)
                } else {
                    Toast.makeText(
                        application.applicationContext,
                        R.string.no_internet,
                        Toast.LENGTH_SHORT
                    ).show()
                }}
        }
    }
    suspend fun getWholeUser(): UserMegaInfo? {
        return withContext(Dispatchers.IO){
            userDao.getEntireUser()?.let { roomToUserMegaInfoAdapter.adapt(it) }
        }
    }
}