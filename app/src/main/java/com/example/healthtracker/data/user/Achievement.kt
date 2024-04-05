package com.example.healthtracker.data.user

data class Achievement(
    val id:Int,
    var image :Int,
    var name:String,
    val goal:Int,
    var unlocked:Boolean? = false
)
