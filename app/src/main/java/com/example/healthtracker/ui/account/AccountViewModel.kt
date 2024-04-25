package com.example.healthtracker.ui.account

import android.app.Application
import android.graphics.Bitmap
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.healthtracker.AuthImpl
import com.example.healthtracker.MainActivity
import com.example.healthtracker.R
import com.example.healthtracker.data.room.RoomToUserMegaInfoAdapter
import com.example.healthtracker.data.user.UserInfo
import com.example.healthtracker.data.user.UserMegaInfo
import com.example.healthtracker.ui.bitmapToBase64
import com.example.healthtracker.ui.isInternetAvailable
import com.example.healthtracker.ui.nullifyStepCounter
import com.example.healthtracker.ui.stopSpeeder
import com.example.healthtracker.ui.stopStepCounterService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AccountViewModel(private val application: Application) : AndroidViewModel(application) {
    private val auth = AuthImpl.getInstance()
    private val userDao = MainActivity.getDatabaseInstance().dao()
    private val roomToUserMegaInfoAdapter = RoomToUserMegaInfoAdapter()
    private val _user = MutableLiveData<UserMegaInfo?>()
    val user: LiveData<UserMegaInfo?> get() = _user
    fun signOut() {
        viewModelScope.launch {
            try {
                sync()
                withContext(Dispatchers.IO) {
                    userDao.dropUser()
                    auth.signOut()
                    nullifyStepCounter()
                    stopStepCounterService()
                    stopSpeeder()
                }
            }catch (e:Exception){
                Toast.makeText(application.applicationContext, R.string.unknown_error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getUser(callback: (UserMegaInfo?) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            callback(roomToUserMegaInfoAdapter.adapt(userDao.getEntireUser()))
        }
    }

    private fun sync() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val syncer = async { userDao.getEntireUser() }.await()
                auth.sync(roomToUserMegaInfoAdapter.adapt(syncer), syncer.userInfo.uid!!)
            }
        }
    }

    fun saveBitmapToDatabase(bitmap: Bitmap) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                userDao.getUserInfo()
                    ?.let {
                        val newImageInfo =
                            UserInfo(
                                it.username,
                                it.uid,
                                bitmapToBase64(bitmap),
                                it.mail,
                                it.theme,
                                it.bgImage,
                                it.totalSteps
                            )
                        it.let {
                            userDao.updateUserInfo(
                                newImageInfo
                            )
                        }
                        if (isInternetAvailable(application.applicationContext)) {
                            auth.saveBitmapToDatabase(bitmap)
                        } else {
                            Toast.makeText(
                                application.applicationContext,
                                R.string.no_internet,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }
    }

    fun getWholeUser() {
        getUser {
            _user.postValue(it)
        }
    }
}
