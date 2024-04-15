package com.example.healthtracker.ui.account.friends.requests

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.healthtracker.AuthImpl
import com.example.healthtracker.MainActivity
import com.example.healthtracker.MyApplication
import com.example.healthtracker.data.user.UserFriends
import com.example.healthtracker.data.user.UserInfo
import kotlinx.coroutines.launch

class FriendRequestViewModel(private val application: Application):AndroidViewModel(application) {
    val authImpl = AuthImpl.getInstance()
    val userDao = MainActivity.getDatabaseInstance().dao()
    var _requestList = MutableLiveData<List<UserFriends>>()
    val requestList: LiveData<List<UserFriends>> get() = _requestList
    fun feedRequests(){
        viewModelScope.launch {
            val requests = authImpl.fetchUserFriends()
            val list = mutableListOf<UserFriends>()
            for(request in requests){
                if (!request.isFriend){
                    list.add(request)
                }
            }
            _requestList.postValue(list)
        }
    }
}