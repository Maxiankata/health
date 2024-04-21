package com.example.healthtracker.ui.account.achievements

import com.example.healthtracker.R
import com.example.healthtracker.data.user.Achievement

object Achievements {
    val achievements = listOf(
        Achievement(
            image = R.drawable.achievement,
            goal = 10000,
            name = R.string.first_achievement,
            id = 1
        ),
        Achievement(
            image = R.drawable.scepter,
            goal = 30000,
            name = R.string.second_achievement,
            id=2
        ),
        Achievement(
            image = R.drawable.wizard_book,
            goal = 50000,
            name = R.string.third_achievement,
            id=3
        ),
        Achievement(
            image = R.drawable.wizard,
            goal = 100000,
            name = R.string.step_wizard,
            id=4
        )

    )
}