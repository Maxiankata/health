package com.example.healthtracker.ui.account.friends.challenges

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.healthtracker.AuthImpl
import com.example.healthtracker.MainActivity
import com.example.healthtracker.MyApplication
import com.example.healthtracker.ui.isInternetAvailable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChallengesDisplayDialogViewModel(application: Application) :
    AndroidViewModel(application) {
    companion object {
        var _challenges = MutableLiveData<List<Challenge>?>()
        fun refeedChallenges() {
            CoroutineScope(Dispatchers.IO).launch {
                val userDao = MainActivity.getDatabaseInstance().dao()
                val challenges = userDao.getEntireUser(
                ).challenges
                _challenges.postValue(challenges)
            }
        }
    }
    val challenges: LiveData<List<Challenge>?> get() = _challenges
    private val userDao = MainActivity.getDatabaseInstance().dao()
    private val authImpl = AuthImpl.getInstance()

    fun refreshChallenges() {
        if (isInternetAvailable(MyApplication.getContext())) {
            viewModelScope.launch {
                val challenges = authImpl.fetchOwnChallenges()
                if (challenges != null) {
                    withContext(Dispatchers.IO) {
                        userDao.updateChallenges(challenges)
                    }
                }
                _challenges.postValue(challenges)
            }
        }
    }

    fun clearChallenges() {
        viewModelScope.launch {
            _challenges.postValue(null)
        }
    }

    fun feedChallenges() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val challenges = userDao.getEntireUser().challenges
                _challenges.postValue(challenges)
            }
        }
    }

}