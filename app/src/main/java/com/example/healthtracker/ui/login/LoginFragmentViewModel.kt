package com.example.healthtracker.ui.login

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getString
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.healthtracker.AuthImpl
import com.example.healthtracker.MainActivity
import com.example.healthtracker.MyApplication
import com.example.healthtracker.R
import com.example.healthtracker.data.room.RoomToUserMegaInfoAdapter
import com.example.healthtracker.data.room.UserMegaInfoToRoomAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginFragmentViewModel(private val application: Application) : AndroidViewModel(application) {
    private val auth = AuthImpl.getInstance()
    private val userDao = MainActivity.getDatabaseInstance().dao()
    private val toRoomAdapter = UserMegaInfoToRoomAdapter()
    private val fromRoomAdapter = RoomToUserMegaInfoAdapter()
    private val _state = MutableLiveData<State>()
    val state: LiveData<State>
        get() = _state

    private val _button_clickable = MutableLiveData<Boolean>()
    val button_clickable : LiveData<Boolean> get() = _button_clickable
    init {
        _button_clickable.postValue(true)
    }
    fun logIn(email: String, password: String, isInternetAvailable: Boolean) {
        _button_clickable.postValue(false)
        _state.value = State.Loading(View.VISIBLE)
        if (!isInternetAvailable) {
            _state.value = State.Notify(getString(MyApplication.getContext(),R.string.no_internet))
            _button_clickable.postValue(true)
            return
        }
        if (email.isEmpty()||password.isEmpty()){
            _state.postValue(State.Notify(getString(MyApplication.getContext(),R.string.empty_field)))
            _button_clickable.postValue(true)
            return
        }
        viewModelScope.launch {
            try {
                if(auth.logIn(email, password)){
                    getUser()
                    delay(3000)
                    _state.postValue(State.LoggedIn(true))
                }else{
                    _button_clickable.postValue(true)
                    _state.postValue(State.Notify(getString(MyApplication.getContext(), R.string.invalid_password)))
                }
            } catch (e: Exception) {
                _button_clickable.postValue(true)
                _state.postValue(State.Notify(e.message ?: "cant login lul"))
            }
        }
    }

    suspend fun getUser() {
        auth.getEntireUser().collect {
            if (it != null) {
                withContext(Dispatchers.IO) {
                    toRoomAdapter.adapt(it)?.let { it1 ->
                        async {
                            userDao.saveUser(it1)
                        }.await()
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
        data class LoggedIn(val flag: Boolean) : State
        data class Notify(val message: String, val visibility: Int = View.GONE) : State
        data class Loading(val loadingVisibility: Int): State
    }
}