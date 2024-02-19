package com.example.healthtracker.ui.account

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthtracker.AuthImpl
import com.example.healthtracker.MainActivity
import com.example.healthtracker.R
import com.example.healthtracker.data.room.RoomToUserMegaInfoAdapter
import com.example.healthtracker.data.user.UserInfo
import com.example.healthtracker.data.user.UserMegaInfo
import com.example.healthtracker.ui.bitmapToBase64
import com.example.healthtracker.ui.isInternetAvailable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.prefs.Preferences
import kotlin.coroutines.coroutineContext

class AccountViewModel(private val application: Application) : AndroidViewModel(application) {
    private val auth = AuthImpl.getInstance()
    private val userDao = MainActivity.getDatabaseInstance(application.applicationContext).dao()
    private val fromRoomAdapter=RoomToUserMegaInfoAdapter()
    suspend fun signOut() {
        viewModelScope.launch {
            auth.signOut()
        }
    }
    suspend fun getUser(){
        withContext(Dispatchers.IO){
            userDao.getEntireUser()?.let { fromRoomAdapter.adapt(it) }
                ?.let { UserMegaInfo.setCurrentUser(it) }
        }
    }
    suspend fun dropTable(){
        withContext(Dispatchers.IO){
            userDao.dropUser()
        }
    }
    suspend fun saveBitmapToDatabase(bitmap: Bitmap) {
            UserMegaInfo.getCurrentUser()?.userInfo?.let {
                val newImageInfo = UserInfo(it.username,it.uid, bitmapToBase64(bitmap),it.mail,it.theme,it.bgImage)
                it.let {
                    userDao.updateImage(
                        newImageInfo
                    )
                }
            }
            val image = bitmapToBase64(bitmap)
            Log.d("SAVED IMAGE TO LOCAL", image)
            if (isInternetAvailable(application.applicationContext)) {
                auth.saveBitmapToDatabase(bitmap)
                Log.d("SAVED IMAGE TO NETWORK", image)
            }else{
                Toast.makeText(application.applicationContext, R.string.no_internet, Toast.LENGTH_SHORT).show()
            }


    }
    fun getWholeUser(): UserMegaInfo? {
        return UserMegaInfo.getCurrentUser()
    }
}