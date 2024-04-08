package com.example.healthtracker.data.user

data class UserAutomaticInfo(
    val steps: StepsInfo?=StepsInfo(),
    val challengesPassed: Int?=0
)