package com.example.healthtracker.ui.account.settings

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthtracker.AuthImpl
import com.example.healthtracker.MainActivity
import com.example.healthtracker.data.room.RoomToUserMegaInfoAdapter
import com.example.healthtracker.ui.stopSpeeder
import com.example.healthtracker.ui.stopStepCounterService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val userDao = MainActivity.getDatabaseInstance().dao()
    private val auth = AuthImpl.getInstance()
    fun removeFriends() {
        viewModelScope.launch {
            val list = auth.fetchUserFriends()
            for (friend in list) {
                auth.removeFriend(friend.uid, list)
            }
        }
    }

    fun deleteUser() {
        viewModelScope.launch {
            auth.deleteCurrentUser()
            withContext(Dispatchers.IO) {
                userDao.dropUser()
            }
            stopStepCounterService()
            stopSpeeder()
            auth.signOut()
        }
    }

}
