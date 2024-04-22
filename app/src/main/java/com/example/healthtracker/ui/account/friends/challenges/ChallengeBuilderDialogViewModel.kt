package com.example.healthtracker.ui.account.friends.challenges

import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthtracker.AuthImpl
import com.example.healthtracker.MainActivity
import com.example.healthtracker.MyApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChallengeBuilderDialogViewModel(private val application: MyApplication) : AndroidViewModel(application) {
    private val userDao = MainActivity.getDatabaseInstance().dao()
    private val authImpl = AuthImpl.getInstance()
    suspend fun sendChallenge(userId:String, challenge: Challenge) {
        viewModelScope.launch {
            val renewedChallengesList = mutableListOf<Challenge>()
            val previousChallenges = authImpl.fetchChallenges(userId)
            if (previousChallenges != null) {
                for (item in previousChallenges){
                    renewedChallengesList.add(item)
                }
                renewedChallengesList.add(challenge)
                authImpl.setChallenges(renewedChallengesList, userId)

            }else{
                renewedChallengesList.add(challenge)
                authImpl.setChallenges(renewedChallengesList, userId)
            }
        }
    }
    suspend fun getAssigner(): Pair<String?, String?> {
        return withContext(Dispatchers.IO){
            Pair(first = userDao.getUserInfo()?.username,second =  userDao.getUserInfo()?.image)
        }
    }

}