package com.example.healthtracker.ui.account.friends.challenges

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.healthtracker.AuthImpl
import com.example.healthtracker.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ChallengesDisplayDialogViewModel(private val application: Application): AndroidViewModel(application) {
    private var _challenges = MutableLiveData<List<Challenge>?>()
    val challenges :LiveData<List<Challenge>?> get() = _challenges
    private val userDao = MainActivity.getDatabaseInstance().dao()
    private val authImpl = AuthImpl.getInstance()

    suspend fun feedChallenges(){
        return withContext(Dispatchers.IO){
            val user = userDao.getBasicInfo()!!.uid
                val challenges = authImpl.fetchChallenges(user!!)
                _challenges.postValue(challenges)
        }
    }
}