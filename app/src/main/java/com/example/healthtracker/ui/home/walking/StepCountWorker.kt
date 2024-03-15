package com.example.healthtracker.ui.home.walking

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.healthtracker.MainActivity
import com.example.healthtracker.data.user.StepsInfo
import com.example.healthtracker.data.user.UserAutomaticInfo

class StepCountWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    val userDao = MainActivity.getDatabaseInstance(context).dao()
    val user = userDao.getEntireUser()
    private val input: Int by lazy {
        inputData.getInt("input", 0)
    }
    override fun doWork(): Result {
        val inputter = UserAutomaticInfo(
            StepsInfo(currentSteps = input)
        )
        userDao.updateUserAutomaticInfo(inputter)
        return Result.success()
    }
}