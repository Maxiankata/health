package com.example.healthtracker.data.user

data class UserAutomaticInfo(
    val steps: StepsInfo?=StepsInfo(),
    var challengesPassed: Int?=0,
    var activeTime:Long? = 0
)