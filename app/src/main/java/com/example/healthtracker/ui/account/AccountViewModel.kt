package com.example.healthtracker.ui.account

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.healthtracker.AuthImpl
import com.example.healthtracker.MainActivity
import com.example.healthtracker.MyApplication
import com.example.healthtracker.R
import com.example.healthtracker.data.room.RoomToUserMegaInfoAdapter
import com.example.healthtracker.data.user.UserInfo
import com.example.healthtracker.data.user.UserMegaInfo
import com.example.healthtracker.ui.bitmapToBase64
import com.example.healthtracker.ui.isInternetAvailable
import com.example.healthtracker.ui.stopStepCounterService
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
            sync()
            withContext(Dispatchers.IO) {
                userDao.dropUser()
                auth.signOut()
                stopStepCounterService()
                Log.d("syn", "sync")
            }
        }
    }

    fun getDays() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                Log.d("userdays in account", userDao.getEntireUser()?.userDays.toString())
            }
        }
    }

    private fun sync() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val syncer = async { userDao.getEntireUser() }.await()
                if (syncer != null) {
                    auth.sync(roomToUserMegaInfoAdapter.adapt(syncer), syncer.userInfo.uid!!)
                } else {
                    Toast.makeText(
                        MyApplication.getContext(),
                        "user from base was null",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    fun saveBitmapToDatabase(bitmap: Bitmap) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                userDao.getBasicInfo()
                    ?.let {
                        val newImageInfo =
                            UserInfo(
                                it.username,
                                it.uid,
                                bitmapToBase64(bitmap),
                                it.mail,
                                it.theme,
                                it.bgImage
                            )
                        it.let {
                            userDao.updateImage(
                                newImageInfo
                            )
                        }
                        val image = bitmapToBase64(bitmap)
                        Log.d("SAVED IMAGE TO LOCAL", image)
                        if (isInternetAvailable(application.applicationContext)) {
                            auth.saveBitmapToDatabase(bitmap)
                            Log.d("SAVED IMAGE TO NETWORK", image)
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
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                userDao.getEntireUser()?.let {
                    _user.postValue(
                        roomToUserMegaInfoAdapter.adapt(
                            it
                        )
                    )
                }
            }
        }
    }
}
