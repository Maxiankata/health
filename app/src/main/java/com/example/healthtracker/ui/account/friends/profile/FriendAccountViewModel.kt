package com.example.healthtracker.ui.account.friends.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.healthtracker.data.user.UserInfo
import com.google.firebase.database.FirebaseDatabase

class FriendAccountViewModel(application: Application):AndroidViewModel(application) {
    private val _friend=MutableLiveData<UserInfo>()
    val friend : LiveData<UserInfo> get() = _friend

}