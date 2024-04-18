package com.example.healthtracker.data.user

data class UserSettingsInfo(
    var language: String? = "en",
    var units: String? = "kg",
    var userGoals: UserGoals?= UserGoals()
)