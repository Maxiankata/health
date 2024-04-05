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
                    Log.d("challenge in list", item.toString())
                    renewedChallengesList.add(item)
                }
                renewedChallengesList.add(challenge)
                Log.d("Challenges werent null, adding new challenge", renewedChallengesList.toString())
                authImpl.setChallenges(renewedChallengesList, userId)
                withContext(Dispatchers.IO){
                    userDao.updateChallenges(renewedChallengesList)
                }
            }else{
                renewedChallengesList.add(challenge)
                Log.d("Challenges were null, adding new challenge", renewedChallengesList.toString())
                authImpl.setChallenges(renewedChallengesList, userId)
                withContext(Dispatchers.IO){
                    userDao.updateChallenges(renewedChallengesList)
                }
            }
        }
    }
    suspend fun getAssigner():String?{
        return withContext(Dispatchers.IO){
            userDao.getUserInfo()?.username
        }
    }

}