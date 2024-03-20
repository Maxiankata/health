package com.example.healthtracker.data.user

data class UserAutomaticInfo(
    val steps: StepsInfo?=StepsInfo(),
    val totalSleepHours: Double?=null,
    val challengesPassed: Int?=0
)