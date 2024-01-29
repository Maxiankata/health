package com.example.healthtracker.data.user

data class UserPutInInfo(
    val waterInfo: WaterInfo? = null,
    val weight: Double? = null
)

data class WaterInfo(
    val waterGoal:Int? = 6,
    val currentWater:Int?=0,
    val waterCompletion:Boolean?=false
)
