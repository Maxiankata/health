package com.example.healthtracker.data.user

import java.util.Date

data class UserDays(
    val dateTime: Date?=null,
    val putInInfo: UserPutInInfo?=null,
    val automaticInfo: UserAutomaticInfo?=null
)
