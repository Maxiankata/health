package com.example.healthtracker.ui.login

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthtracker.AuthImpl
import com.example.healthtracker.MainActivity
import com.example.healthtracker.data.room.RoomToUserMegaInfoAdapter
import com.example.healthtracker.data.room.UserMegaInfoToRoomAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class LoginFragmentViewModel(private val application: Application) : AndroidViewModel(application) {
    private val auth = AuthImpl.getInstance()
    private val userDao = MainActivity.getDatabaseInstance().dao()
    private val toRoomAdapter = UserMegaInfoToRoomAdapter()
    private val fromRoomAdapter = RoomToUserMegaInfoAdapter()


    suspend fun logIn(email: String, password: String): Boolean {
        return auth.logIn(email, password)
    }

    suspend fun getUser() {
        Log.d("running getUser() in login", "")

        auth.getEntireUser().collect {
            Log.d("login collector ", it.toString())
            if (it != null) {
                Log.d("user from firebase", it.toString())
                withContext(Dispatchers.IO) {
                    toRoomAdapter.adapt(it)?.let { it1 ->
                        userDao.saveUser(it1)
                        Log.d("user from dao", userDao.getEntireUser().toString())
                    }
                }
            }
        }
    }
    private val permissions = arrayOf(
        Manifest.permission.FOREGROUND_SERVICE,
        Manifest.permission.POST_NOTIFICATIONS,
        Manifest.permission.ACTIVITY_RECOGNITION,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.RECEIVE_BOOT_COMPLETED,
        Manifest.permission.SCHEDULE_EXACT_ALARM,
        Manifest.permission.USE_EXACT_ALARM
    )

    private val PERMISSION_REQUEST_CODE = 123

    fun requestPermissionsUntilGranted(fragment: Fragment) {
        viewModelScope.launch {
            requestPermissions(fragment)
        }
    }

    private suspend fun requestPermissions(fragment: Fragment) {
        if (!checkPermissions(fragment)) {
            delay(3000)
            ActivityCompat.requestPermissions(
                fragment.requireActivity(),
                permissions,
                PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun checkPermissions(fragment: Fragment): Boolean {
        var allPermissionsGranted = true
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(
                    fragment.requireContext(),
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                allPermissionsGranted = false
                break
            }
        }
        return allPermissionsGranted
    }
    sealed interface State {
        data class LoggedIn(val flag: Boolean): State
        data class Notify(val message: String): State
    }
}