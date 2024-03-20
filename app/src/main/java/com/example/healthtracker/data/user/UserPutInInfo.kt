package com.example.healthtracker.data.user

data class UserPutInInfo(
    var waterInfo: WaterInfo? = WaterInfo(),
    val weight: Double? = 0.0
)

data class WaterInfo(
    var currentWater: Int? = 0,
    val waterCompletion: Boolean? = false
)
