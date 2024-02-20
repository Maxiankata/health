package com.example.healthtracker.ui.home.walking

import android.app.Application
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.work.WorkerParameters
import androidx.work.Worker


class WalkResetTask (context: Context, params: WorkerParameters) : Worker(context, params){
    @RequiresApi(Build.VERSION_CODES.O)
    override fun doWork(): Result {
        val viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(applicationContext as Application).create(WalkViewModel::class.java)
        return try {
                viewModel.nullifySteps()
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}