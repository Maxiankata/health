package com.example.healthtracker.data.user

data class UserPutInInfo(
    var waterInfo: WaterInfo? = WaterInfo(),
    var weight: Double? = 0.0,
    var sleepDuration:String?="",
    var units: String? = ""
)

data class WaterInfo(
    var currentWater: Int? = 0,
    var waterGoal:Int?=6,
    val waterCompletion: Boolean? = false
)
