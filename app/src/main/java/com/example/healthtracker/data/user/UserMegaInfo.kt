package com.example.healthtracker.data.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

data class UserMegaInfo(
    var userInfo: UserInfo,
    var userAutomaticInfo: UserAutomaticInfo? = null,
    val userFriends: List<UserInfo>? = listOf(),
    val userPutInInfo: UserPutInInfo? = null,
    val userSettingsInfo: UserSettingsInfo? = null,
    val userDays:List<UserDays>? = null
)
