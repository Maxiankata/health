package com.example.healthtracker.data.user

import com.example.healthtracker.ui.account.friends.challenges.Challenge

data class UserMegaInfo(
    var userInfo: UserInfo,
    var userAutomaticInfo: UserAutomaticInfo? = UserAutomaticInfo(),
    val userFriends: List<UserInfo>? = listOf(),
    val userPutInInfo: UserPutInInfo? = UserPutInInfo(),
    val userSettingsInfo: UserSettingsInfo? = UserSettingsInfo(),
    val userDays:List<UserDays>? = null,
    val challenges:List<Challenge>? = listOf(),
    val achievements:List<Achievement>?= listOf()
)
