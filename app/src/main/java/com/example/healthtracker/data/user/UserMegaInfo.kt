package com.example.healthtracker.data.user

data class UserMegaInfo(
    var userInfo: UserInfo,
    var userAutomaticInfo: UserAutomaticInfo? = null,
    val userFriends: List<UserInfo>? = listOf(),
    val userPutInInfo: UserPutInInfo? = null,
    val userSettingsInfo: UserSettingsInfo? = UserSettingsInfo(),
    val userDays:List<UserDays>? = null,
)
