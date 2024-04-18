package com.example.healthtracker.ui.account.settings

import android.content.Context
import android.util.Log
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthtracker.AuthImpl
import com.example.healthtracker.MainActivity
import com.example.healthtracker.MainViewModel
import com.example.healthtracker.MyApplication
import com.example.healthtracker.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LanguageChangeDialogViewModel(private val application: MyApplication):AndroidViewModel(application) {
    val userDao = MainActivity.getDatabaseInstance().dao()
    val auth = AuthImpl.getInstance()
    private val mainViewModel = MainViewModel(application)
    fun updateUnits(string: String){
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (string.isNotEmpty()) {
                    val previousUnits = userDao.getUserSettings().units
                    val userSettings = userDao.getUserSettings()
                    val userPutInInfo = userDao.getPutInInfo()
                    userSettings.units = string
                    userDao.updateUserSettings(userSettings)
                    if (previousUnits != string) {
                        if (previousUnits == "kg") {
                            userPutInInfo?.weight = userPutInInfo?.weight?.times(2.2)
                            if (userPutInInfo != null) {
                                userDao.updateUserPutInInfo(userPutInInfo)
                            }
                        } else if (previousUnits == "lbs") {
                            userPutInInfo?.weight = userPutInInfo?.weight?.times(0.45)
                            if (userPutInInfo != null) {
                                userDao.updateUserPutInInfo(userPutInInfo)
                            }
                        }
                    }
                }
            }
        }
    }
    fun updateLanguage(string: String, context:Context){
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (string.isNotEmpty()){
                    val userSettings = userDao.getUserSettings()
                    if (string==getString(context,R.string.english)){
                        userSettings.language = "en"
                        userDao.updateUserSettings(userSettings)
                        mainViewModel.updatePrefLanguage(context,
                            userSettings.language!!
                        )
                        auth.updateSettings(userDao.getUserSettings())
                    }
                    if (string ==getString(context,R.string.bulgarian)){
                        userSettings.language = "bg"
                        userDao.updateUserSettings(userSettings)
                        mainViewModel.updatePrefLanguage(context, userSettings.language!!)
                        auth.updateSettings(userDao.getUserSettings())
                    }
                }
            }
        }
    }
}