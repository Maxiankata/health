package com.example.healthtracker.ui.account

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthtracker.AuthImpl
import com.example.healthtracker.MainActivity
import com.example.healthtracker.data.user.UserMegaInfo
import com.example.healthtracker.ui.bitmapToBase64
import com.example.healthtracker.ui.isInternetAvailable
import kotlinx.coroutines.launch

class AccountViewModel(application: Application) : AndroidViewModel(application) {
    private val auth = AuthImpl.getInstance()
    private val userDao = MainActivity.getDatabaseInstance(application.applicationContext).dao()

    suspend fun signOut() {
        viewModelScope.launch {
            auth.signOut()
        }
    }

    suspend fun saveBitmapToDatabase(bitmap: Bitmap, context: Context) {
        UserMegaInfo.getCurrentUser()?.userInfo?.uid?.let {
            userDao.updateImage(
                it, bitmapToBase64(bitmap)
            )
        }
        val image = bitmapToBase64(bitmap)
        Log.d("SAVED IMAGE TO LOCAL", image)
        viewModelScope.launch {
            if (isInternetAvailable(context)) {
                auth.saveBitmapToDatabase(bitmap)
                Log.d("SAVED IMAGE TO LOCAL", image)
            } else {
                Toast.makeText(context, "Saving Locally Only", Toast.LENGTH_SHORT).show()
            }
        }

    }

    fun getWholeUser(): UserMegaInfo? {
        return UserMegaInfo.getCurrentUser()
    }
}