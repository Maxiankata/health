package com.example.healthtracker.ui.account.settings

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthtracker.MainActivity
import com.example.healthtracker.MyApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LanguageChangeDialogViewModel(private val application: MyApplication):AndroidViewModel(application) {
    val userDao = MainActivity.getDatabaseInstance().dao()
    fun updateUnits(string: String){
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val userSettings = userDao.getUserSettings()
                userSettings?.units = string
                if (userSettings != null) {
                    userDao.updateUserSettings(userSettings)
                }
            }
        }
    }
    fun updateLanguage(string: String){
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val userSettings = userDao.getUserSettings()
                userSettings?.language = string
                if (userSettings != null) {
                    userDao.updateUserSettings(userSettings)
                }
            }
        }
    }
}