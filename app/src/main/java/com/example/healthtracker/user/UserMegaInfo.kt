package com.example.healthtracker.user

data class UserMegaInfo(
    val userInfo: UserInfo?=null,
    val userAutomaticInfo: UserAutomaticInfo?=null,
    val userFriendInfo: UserFriendInfo?=null,
    val userPutInInfo: UserPutInInfo?=null,
    val userSettingsInfo: UserSettingsInfo? = null)
