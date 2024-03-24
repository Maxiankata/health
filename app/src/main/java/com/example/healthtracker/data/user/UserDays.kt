package com.example.healthtracker.data.user

import androidx.room.TypeConverters
import com.example.healthtracker.CalendarTypeConverter
import com.example.healthtracker.data.Challenge
import com.google.type.DateTime
import java.util.Calendar
import java.util.Date

data class UserDays(
    val putInInfo: UserPutInInfo?=null,
    val automaticInfo: UserAutomaticInfo?=null,
    val challenges:List<Challenge>?=null,
    val dateTime: String,
)
