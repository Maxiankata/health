package com.example.healthtracker.ui.account.friends.challenges

import com.example.healthtracker.ui.home.speeder.ActivityEnum

data class Challenge(
    val id:Int,
    val image:String,
    val assigner:String,
    val challengeType: ActivityEnum,
    val challengeDuration: String,
    var challengeCompletion: Boolean
)