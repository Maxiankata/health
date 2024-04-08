package com.example.healthtracker.ui.account.friends.challenges

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.healthtracker.AuthImpl
import com.example.healthtracker.MainActivity
import com.example.healthtracker.MyApplication
import com.example.healthtracker.ui.isInternetAvailable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChallengesDisplayDialogViewModel(private val application: Application) :
    AndroidViewModel(application) {
    companion object {
        var _challenges = MutableLiveData<List<Challenge>?>()

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

    fun feedChallenges() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val challenges = userDao.getEntireUser(
                ).challenges
                if (challenges != null) {
                    for (challenge in challenges) {
                        Log.d(
                            "challenge name and status",
                            "${challenge.challengeType}, ${challenge.challengeCompletion}, ${challenge.id}"
                        )
                    }
                }
                _challenges.postValue(challenges)
            }
        }
    }
}