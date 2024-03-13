package com.example.healthtracker.ui.settings

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
import com.example.healthtracker.databinding.RgbPickerDialogBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsViewModel(private val application: Application) : AndroidViewModel(application) {
    private val userDao = MainActivity.getDatabaseInstance(application.applicationContext).dao()
    private val auth = AuthImpl.getInstance()

    suspend fun deleteUser() {
        viewModelScope.launch {
            auth.deleteCurrentUser()
            auth.signOut()
        }
        withContext(Dispatchers.IO) {
            userDao.dropUser()
        }
    }
}
