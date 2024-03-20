package com.example.healthtracker.data.user

data class UserSettingsInfo(
    var language: String? = "english",
    var units: String? = "kg",
    val userGoals: UserGoals
)