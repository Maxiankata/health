package com.example.healthtracker.ui.account

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthtracker.AuthImpl
import com.example.healthtracker.data.user.UserInfo
import kotlinx.coroutines.launch

class AccountViewModel : ViewModel() {
    private val auth = AuthImpl.getInstance()
    suspend fun signOut() {
        viewModelScope.launch {
            auth.signOut()
        }
    }
    suspend fun saveBitmapToDatabase(bitmap: Bitmap){
        auth.saveBitmapToDatabase(bitmap)
    }
    suspend fun getUser(): UserInfo? {
        return auth.getCurrentUser()
    }
}