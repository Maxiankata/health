package com.example.healthtracker.user

data class UserAutomaticInfo(
    val todaySteps: Int?=null,
    val totalSteps: Int?=null,
    val totalSleepHours: Double?=null,
    val challengesPassed: Int?=null
)