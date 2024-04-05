package com.example.healthtracker.ui.account.achievements

import com.example.healthtracker.R
import com.example.healthtracker.data.user.Achievement

object Achievements {
    val achievements = listOf(
        Achievement(
            image = R.drawable.power_walking_icon,
            goal = 10000,
            name = "little newbie",
            id = 1
        ),
        Achievement(
            image = R.drawable.jogging_icon,
            goal = 20000,
            name = "pfft easy",
            id=2
        ),
        Achievement(
            image = R.drawable.running_icon,
            goal = 30000,
            name = "getting heated",
            id=3
        ),
        Achievement(
            image = R.drawable.cycling_icon,
            goal = 50000,
            name = "calm down!!",
            id=4
        )

    )
}