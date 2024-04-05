package com.example.healthtracker.ui.account.settings.colorchange

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthtracker.MainActivity
import com.example.healthtracker.data.user.UserInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ColorChangerViewModel(private val application: Application) :
    AndroidViewModel(application) {
    private val userDao = MainActivity.getDatabaseInstance().dao()
    fun getUser(callback: (UserInfo?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = userDao.getUserInfo()
            withContext(Dispatchers.Main) {
                callback(user)
            }
        }
    }

    fun updateUser(userInfo: UserInfo) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                userDao.updateUserInfo(userInfo)
            }
        }
    }
}