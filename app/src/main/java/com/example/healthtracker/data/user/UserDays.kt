package com.example.healthtracker.data.user

import com.example.healthtracker.ui.account.friends.challenges.Challenge

data class UserDays(
    val putInInfo: UserPutInInfo?=null,
    val automaticInfo: UserAutomaticInfo?=null,
    val challenges:List<Challenge>?=null,
    val dateTime: String,
)
