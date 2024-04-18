package com.example.healthtracker

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import com.example.healthtracker.data.room.RoomToUserMegaInfoAdapter
import com.example.healthtracker.data.room.UserMegaInfoToRoomAdapter
import com.example.healthtracker.data.user.UserSettingsInfo
import com.example.healthtracker.ui.home.speeder.SpeederServiceBoolean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class MainViewModel(private val application: Application) : AndroidViewModel(application) {
    private val auth = AuthImpl.getInstance()
    private val userDao = MainActivity.getDatabaseInstance().dao()
    private var fromRoomAdapter = RoomToUserMegaInfoAdapter()
    private var toRoomAdapter = UserMegaInfoToRoomAdapter()

    private val _selectedLanguage = MutableLiveData<String>()
    val selectedLanguage: LiveData<String> get() = _selectedLanguage

    val settings :LiveData<UserSettingsInfo> = userDao.getUserSettingsLiveData()
    fun syncCloud() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val user = userDao.getEntireUser()
                user.let {
                    fromRoomAdapter.adapt(it)
                }?.let {
//                    auth.sync(it)

                }
            }
        }
    }

    fun syncTimer() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                SpeederServiceBoolean._activityTime.postValue(
                    userDao.getAutomaticInfo()?.activeTime ?: 0
                )
            }
        }
    }
    fun updateLanguage(context: Context) {
        viewModelScope.launch {
            applyLocale(context)
        }
    }

    suspend fun updatePrefLanguage(context: Context, newLanguage: String) {
        withContext(Dispatchers.IO) {
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            val editor = prefs.edit()
            editor.putString("selected_language", newLanguage)
            editor.apply()
        }
    }
    fun getSelectedLanguage(context: Context): String {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getString("selected_language", "en") ?: "en"
    }
    private suspend fun applyLocale(context: Context) {
        withContext(Dispatchers.IO) {
            val locale = Locale(getSelectedLanguage(context))
            Locale.setDefault(locale)
            val config = Configuration(context.resources.configuration)
            config.setLocale(locale)
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
        }
    }
}