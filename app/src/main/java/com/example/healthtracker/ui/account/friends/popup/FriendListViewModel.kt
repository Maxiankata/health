package com.example.healthtracker.ui.account.friends.popup

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthtracker.AuthImpl
import com.example.healthtracker.MainActivity
import com.example.healthtracker.data.user.UserInfo
import kotlinx.coroutines.launch

class FriendListViewModel : ViewModel() {
    private val _usersList = MutableLiveData<MutableList<UserInfo>?>()
    val usersList: MutableLiveData<MutableList<UserInfo>?> get() = _usersList
    private val _friendsList = MutableLiveData<MutableList<UserInfo>?>()
    val friendsList: LiveData<MutableList<UserInfo>?> get() = _friendsList
    private var _searchState = MutableLiveData<Boolean>()
    private val auth = AuthImpl.getInstance()
    val searchState: LiveData<Boolean> get() = _searchState

    init {
        _usersList.postValue(null)
        _searchState.value = false

    }

    private var friendsInfoList = mutableListOf<UserInfo>()

    suspend fun fetchAllUsersInfo() {
        viewModelScope.launch {
            val allUsers = auth.fetchAllUsersInfo()
            val filteredUsers = allUsers.filter { user ->
                !friendsInfoList.any { friend -> friend.uid == user.uid }
            } as MutableList<UserInfo>
            _usersList.postValue(filteredUsers)
        }
    }
    suspend fun fetchSearchedUsers(string: String){
        viewModelScope.launch{
            val searchedUsers = auth.fetchSearchedUsers(string)
            val filteredUsers = searchedUsers.filter { user ->
                !friendsInfoList.any { friend -> friend.uid == user.uid }
            } as MutableList<UserInfo>
            _usersList.postValue(filteredUsers)
        }
    }
    suspend fun fetchSearchedFriends(string: String){
        viewModelScope.launch{
            val searchedUsers = auth.fetchSearchedFriends(string)
            val filteredUsers = searchedUsers.filter { user ->
                !friendsInfoList.any { friend -> friend.uid == user.uid }
            } as MutableList<UserInfo>
            _usersList.postValue(filteredUsers)
        }
    }
    suspend fun removeFriend(userId: String) {
        viewModelScope.launch {
            auth.removeFriend(userId, friendsInfoList)
            _friendsList.postValue(auth.fetchUserFriends() as MutableList<UserInfo>?)
        }
    }

    suspend fun fetchUserFriends() {
        viewModelScope.launch {
                friendsInfoList = auth.fetchUserFriends() as MutableList<UserInfo>
                _friendsList.postValue(friendsInfoList)
                _usersList.postValue(friendsInfoList)
            Log.d("Fetched Friends", _usersList.value.toString())
        }
    }


    suspend fun getUser(uid: String): UserInfo? {
        return auth.getUserInfo(uid)
    }

    suspend fun addFriend(userId: String) {
        viewModelScope.launch {
            auth.addFriend(userId)
            _friendsList.postValue(auth.fetchUserFriends() as MutableList<UserInfo>?)
        }
    }
    fun clearList(){
        _usersList.value?.clear()
        _usersList.postValue(_usersList.value)
        Log.d("FriendListPostClear", _usersList.value.toString())

    }
    fun switchSearchState() {
        _searchState.postValue(!_searchState.value!!)
    }

}

