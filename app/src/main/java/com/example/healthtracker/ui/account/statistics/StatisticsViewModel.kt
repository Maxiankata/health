package com.example.healthtracker.ui.account.statistics

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.healthtracker.AuthImpl
import com.example.healthtracker.MainActivity
import com.example.healthtracker.MyApplication
import com.example.healthtracker.data.user.UserDays
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StatisticsViewModel(private val application: Application) : AndroidViewModel(application) {
    private val userDao = MainActivity.getDatabaseInstance().dao()
    private val authImpl = AuthImpl.getInstance()
    private val _userDays = MutableLiveData<List<UserDays?>?>()
    val userDays: LiveData<List<UserDays?>?> get() = _userDays



    fun getUserDays() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _userDays.postValue(userDao.getEntireUser()?.userDays)
            }
        }
    }
}