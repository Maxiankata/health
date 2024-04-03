package com.example.healthtracker.data.user

data class UserPutInInfo(
    var waterInfo: WaterInfo? = WaterInfo(),
    var weight: Double? = 0.0,
    var sleepDuration:String?=""
)

data class WaterInfo(
    var currentWater: Int? = 0,
    val waterCompletion: Boolean? = false
)
