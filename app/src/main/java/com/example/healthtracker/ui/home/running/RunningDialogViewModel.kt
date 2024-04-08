package com.example.healthtracker.ui.home.running

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.healthtracker.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RunningDialogViewModel(private val application: Application) : AndroidViewModel(application) {
    private val userDao = MainActivity.getDatabaseInstance().dao()
    private val _userMetric = MutableLiveData<String>()
    val userMetric :LiveData<String> get() = _userMetric

    fun getMetric(){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                val metric = userDao.getUserSettings()?.units
                if (!metric.isNullOrEmpty()){
                    _userMetric.postValue(metric!!)
                }
            }
        }
    }
}