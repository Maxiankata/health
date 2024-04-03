package com.example.healthtracker.data.user

data class StepsInfo(
    val currentSteps:Int?=0,
    val currentCalories:Int?=0,
    var stepsGoal:Int?=6000,
    var caloriesGoal:Int?=240
)
