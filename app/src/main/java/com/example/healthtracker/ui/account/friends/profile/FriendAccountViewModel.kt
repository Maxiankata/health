package com.example.healthtracker.ui.account.friends.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.healthtracker.AuthImpl
import com.example.healthtracker.data.user.UserDays
import com.example.healthtracker.data.user.UserInfo
import kotlinx.coroutines.launch

class FriendAccountViewModel(application: Application) : AndroidViewModel(application) {
    private val authImpl = AuthImpl.getInstance()
    private val _user = MutableLiveData<Pair<UserInfo, List<UserDays>?>>()
    val user: LiveData<Pair<UserInfo, List<UserDays>?>> get() = _user

    fun feedUser(id: String) {
        viewModelScope.launch {
            authImpl.getUserInfo(id)?.let {
                _user.postValue(Pair(authImpl.getUserInfo(id)!!, authImpl.fetchFriendDays(id)))
            }
        }
    }
}