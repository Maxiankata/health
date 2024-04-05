package com.example.healthtracker.ui.account.friends.challenges

import com.example.healthtracker.ui.home.speeder.ActivityEnum

data class Challenge(
    val id:Int,
    val image:String,
    val assigner:String,
    val challengeType: ActivityEnum,
    val challengeDuration: String,
    var challengeCompletion: Boolean
){
    companion object {

        private const val DELIMITER = "|"
        fun fromString(str: String): Challenge {
            val parts = str.split(DELIMITER)
            return Challenge(
                id = parts[0].toInt(),
                image = parts[1],
                assigner = parts[2],
                challengeType = ActivityEnum.valueOf(parts[3]),
                challengeDuration = parts[4],
                challengeCompletion = parts[5].toBoolean()
            )
        }
    }

    override fun toString(): String {
        return "$id$DELIMITER$image$DELIMITER$assigner$DELIMITER${challengeType.name}$DELIMITER$challengeDuration$DELIMITER$challengeCompletion"
    }
}