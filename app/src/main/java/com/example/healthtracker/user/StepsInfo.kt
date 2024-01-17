package com.example.healthtracker.user


data class StepsInfo(
    val totalSteps:Int?=null,
    val totalCalories:Int?=null,
    val onLeaveSteps:Int?=null,
    val onLogSteps:Int?=null,
    val currentSteps:Int?=null,
    val currentCalories:Int?=null
)
