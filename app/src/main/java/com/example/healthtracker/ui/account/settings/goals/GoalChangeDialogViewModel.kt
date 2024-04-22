package com.example.healthtracker.ui.account.settings.goals

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.healthtracker.MainActivity
import com.example.healthtracker.MyApplication
import com.example.healthtracker.data.user.UserGoals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GoalChangeDialogViewModel(application: MyApplication) : AndroidViewModel(application) {

    private val userDao = MainActivity.getDatabaseInstance().dao()

    companion object {
        private val _userGoals = MutableLiveData<UserGoals?>()
        val userGoals: LiveData<UserGoals?> get() = _userGoals
    }

    suspend fun getUserGoals(): UserGoals? {
        return withContext(Dispatchers.IO) {
            val user = userDao.getEntireUser().userSettingsInfo?.userGoals
            _userGoals.postValue(user)
            user
        }
    }

    suspend fun updateGoal(userGoals: UserGoals) {
        withContext(Dispatchers.IO) {
            val userSettingsInfo = userDao.getUserSettings()
            userSettingsInfo.userGoals = userGoals
            _userGoals.postValue(userGoals)
            userDao.updateUserSettings(userSettingsInfo)
        }
    }
}