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
                val previousUnits = userDao.getUserSettings()?.units
                val userSettings = userDao.getUserSettings()
                val userPutInInfo = userDao.getPutInInfo()
                userSettings?.units = string
                if (userSettings != null) {
                    userDao.updateUserSettings(userSettings)
                }
                if (previousUnits!=string){
                    if (previousUnits == "kg"){
                        userPutInInfo?.weight = userPutInInfo?.weight?.times(2.2)
                        if (userPutInInfo != null) {
                            userDao.updateUserPutInInfo(userPutInInfo)
                        }
                    }else if (previousUnits=="lbs"){
                        userPutInInfo?.weight = userPutInInfo?.weight?.times(0.45)
                        if (userPutInInfo != null) {
                            userDao.updateUserPutInInfo(userPutInInfo)
                        }
                    }
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