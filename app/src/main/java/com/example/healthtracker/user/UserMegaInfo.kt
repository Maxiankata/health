package com.example.healthtracker.user

data class UserMegaInfo(
    val userInfo: UserInfo?=null,
    val userAutomaticInfo: UserAutomaticInfo?=null,
    val userFriends: List<UserInfo>?=null,
    val userPutInInfo: UserPutInInfo?=null,
    val userSettingsInfo: UserSettingsInfo? = null
)
