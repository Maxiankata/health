package com.example.healthtracker.ui.account.settings

import android.app.AlertDialog
import android.app.Application
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthtracker.AuthImpl
import com.example.healthtracker.MainActivity
import com.example.healthtracker.R
import com.example.healthtracker.data.room.RoomToUserMegaInfoAdapter
import com.example.healthtracker.data.user.UserMegaInfo
import com.example.healthtracker.data.user.UserSettingsInfo
import com.example.healthtracker.databinding.RgbPickerDialogBinding
import com.example.healthtracker.ui.stopSpeeder
import com.example.healthtracker.ui.stopStepCounterService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsViewModel(private val application: Application) : AndroidViewModel(application) {
    private val userDao = MainActivity.getDatabaseInstance().dao()
    private val auth = AuthImpl.getInstance()
    private val roomToUserMegaInfoAdapter = RoomToUserMegaInfoAdapter()

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
