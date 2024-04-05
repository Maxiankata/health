package com.example.healthtracker.ui.login.register

import android.app.Application
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
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
import com.example.healthtracker.ui.login.LoginFragmentViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterViewModel(private val application: Application) : AndroidViewModel(application) {
    private val auth = AuthImpl.getInstance()
    private val userDao = MainActivity.getDatabaseInstance().dao()
    private var toRoomAdapter = UserMegaInfoToRoomAdapter()
    private var fromRoomAdapter = RoomToUserMegaInfoAdapter()
    private val _state = MutableLiveData<State>()
    val state: LiveData<State>
        get() = _state

    suspend fun register(
        email: String, password: String, name: String, isInternetAvailable: Boolean
    ) {
        _state.value = State.Loading(View.VISIBLE)
        if (!isInternetAvailable) {
            _state.value = State.Notify("no wifi")
            return
        }
        if (email.isEmpty() || password.isEmpty()) {
            _state.postValue(
                State.Notify(
                    ContextCompat.getString(
                        MyApplication.getContext(), R.string.empty_field
                    )
                )
            )
            return
        }
        viewModelScope.launch {
            try {
                if (auth.createAcc(email, password, name)) {
                    getUser()
                        _state.postValue(State.LoggedIn(true))
                } else {
                    Log.d("state", "loading visibilit")
                    _state.postValue(
                        State.Notify(
                            ContextCompat.getString(
                                MyApplication.getContext(),
                                R.string.invalid_password
                            )
                        )
                    )
                }
            } catch (e: Exception) {
                _state.postValue(State.Notify(e.message ?: "cant login lul"))
            }
        }
    }

    fun getUser() {
        viewModelScope.launch {
            async {

            auth.getEntireUser().collect {
                if (it != null) {
                    withContext(Dispatchers.IO) {
                        toRoomAdapter.adapt(it)?.let { it1 ->
                            userDao.saveUser(it1)
                            userDao.getEntireUser().let { it2 -> fromRoomAdapter.adapt(it2) }
                            Log.d("user in register", userDao.getEntireUser().toString())
                        }
                    }
                }
            }
            }.await()

        }
    }

    sealed interface State {
        data class LoggedIn(val flag: Boolean) : State
        data class Notify(val message: String, val visibility: Int = View.GONE) : State
        data class Loading(val loadingVisibility: Int) : State
    }
}