package com.example.healthtracker.data.user

data class StepsInfo(
    val totalSteps:Int?=0,
    val totalCalories:Int?=0,
    val currentSteps:Int?=0,
    val currentCalories:Int?=0,
    val calorieGoal:Int?=240,
    val stepGoal:Int?=6000
)
