package com.example.healthtracker

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.preference.PreferenceManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.healthtracker.data.user.UserSettingsInfo
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val userDao = MainActivity.getDatabaseInstance().dao()
    val settings: LiveData<UserSettingsInfo> = userDao.getUserSettingsLiveData()

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

    private fun getSelectedLanguage(context: Context): String {
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