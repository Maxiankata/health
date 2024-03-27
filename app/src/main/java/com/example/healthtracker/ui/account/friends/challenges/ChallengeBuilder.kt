package com.example.healthtracker.ui.account.friends.challenges

data class Challenge(
    val assigner:String,
    val challengeType: ChallengeType,
    val challengeDuration: String,
    val challengeCompletion: Boolean
)
enum class ChallengeType {
    WALKING,
    CYCLING,
    RUNNING,
    JOGGING,
    POWER_WALKING,

}