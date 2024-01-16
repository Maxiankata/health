package com.example.healthtracker.user

data class UserAutomaticInfo(
    val steps:StepsInfo?=null,
    val totalSleepHours: Double?=null,
    val challengesPassed: Int?=null
)