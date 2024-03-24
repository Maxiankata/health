package com.example.healthtracker.data

import java.time.Duration

data class Challenge(
    val assigner:String,
    val challengeType: ChallengeType,
    val challengeDuration: String,
    val challengeCompletion: Boolean
)

interface ChallengeBuilder {
    fun buildChallenge(challenge: Challenge)
}
enum class DurationType{
    MINUTES,
    STEPS
}
enum class ChallengeType {
    WALKING,
    CYCLING,
    RUNNING,
    HIKING,
    POWER_WALKING,

}