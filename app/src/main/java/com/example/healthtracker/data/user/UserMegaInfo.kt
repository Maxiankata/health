package com.example.healthtracker.data.user

data class UserMegaInfo(
    val userInfo: UserInfo,
    val userAutomaticInfo: UserAutomaticInfo?=null,
    val userFriends: List<UserInfo>?= listOf(),
    val userPutInInfo: UserPutInInfo?=null,
    val userSettingsInfo: UserSettingsInfo? = null
)
