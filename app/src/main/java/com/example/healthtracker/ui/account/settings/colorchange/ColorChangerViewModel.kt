package com.example.healthtracker.ui.account.settings.colorchange

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.healthtracker.AuthImpl
import com.example.healthtracker.MainActivity
import com.example.healthtracker.MyApplication
import com.example.healthtracker.R
import com.example.healthtracker.data.user.UserInfo
import com.example.healthtracker.ui.isInternetAvailable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ColorChangerViewModel(private val application: Application) :
    AndroidViewModel(application) {
    private val userDao = MainActivity.getDatabaseInstance().dao()
    private val authImpl = AuthImpl.getInstance()
    private val _user = MutableLiveData<UserInfo>()
    val user :LiveData<UserInfo> get() = _user
    fun getUser() {
        viewModelScope.launch{
            withContext(Dispatchers.IO) {
                _user.postValue(userDao.getUserInfo())
            }
        }
    }

    fun updateUser(userInfo: UserInfo) {
        viewModelScope.launch {
            if (isInternetAvailable(MyApplication.getContext())){
                authImpl.updateUserInfo(userInfo)
            }else{
                Toast.makeText(MyApplication.getContext(), R.string.local_update, Toast.LENGTH_SHORT).show()
            }
            withContext(Dispatchers.IO) {
                userDao.updateUserInfo(userInfo)
            }

        }
    }
}