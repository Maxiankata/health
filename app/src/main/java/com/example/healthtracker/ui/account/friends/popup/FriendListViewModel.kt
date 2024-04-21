package com.example.healthtracker.ui.account.friends.popup

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthtracker.AuthImpl
import com.example.healthtracker.MainActivity
import com.example.healthtracker.data.user.UserFriends
import com.example.healthtracker.data.user.UserInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FriendListViewModel : ViewModel() {
    private val _usersList = MutableLiveData<MutableList<UserInfo>?>()
    val usersList: MutableLiveData<MutableList<UserInfo>?> get() = _usersList
    private val _friendsList = MutableLiveData<MutableList<UserFriends>?>()
    val friendsList: LiveData<MutableList<UserFriends>?> get() = _friendsList
    private val auth = AuthImpl.getInstance()
    private val userDao = MainActivity.getDatabaseInstance().dao()

    companion object {
        private var _searchState = MutableLiveData<Boolean>()
        val searchState: LiveData<Boolean> get() = _searchState
    }

    init {
        _usersList.postValue(null)
        _searchState.value = false

    }

    private var friendsInfoList = mutableListOf<UserFriends>()

    fun fetchSearchedUsers(string: String) {
        viewModelScope.launch {
            val searchedUsers = auth.fetchSearchedUsers(string)
            val filteredUsers = searchedUsers.filter { user ->
                !friendsInfoList.any { friend -> friend.uid == user.uid }
            } as MutableList<UserInfo>
            val currentUser = auth.getCurrentUser()?.userInfo
            Log.d("current user", currentUser?.username.toString())
            if (filteredUsers.contains(currentUser)) {
                filteredUsers.remove(currentUser)
                Log.d("removed current user", "")
            }
            _usersList.postValue(filteredUsers)
        }
    }

    fun fetchSearchedFriends(string: String) {
        viewModelScope.launch {
            val searchedUsers = auth.fetchSearchedFriends(string)
            val filteredUsers = searchedUsers.filter { user ->
                !friendsInfoList.any { friend -> friend.uid == user.uid }
            } as MutableList<UserInfo>
            _usersList.postValue(filteredUsers)
        }
    }

    fun removeFriend(userId: String) {
        viewModelScope.launch {
            auth.removeFriend(userId, friendsInfoList)
            _friendsList.postValue(auth.fetchUserFriends() as MutableList<UserFriends>?)
        }
    }

    fun fetchUserFriends() {
        viewModelScope.launch {
            val timelyList = auth.fetchUserFriends() as MutableList<UserFriends>
            val friendlist = mutableListOf<UserFriends>()
            val userlist = mutableListOf<UserInfo>()
            for (user in timelyList){
                if(user.isFriend){
                    friendlist.add(user)
                    auth.getUserInfo(user.uid)?.let { userlist.add(it) }
                }
            }
            _friendsList.postValue(friendlist)
            _usersList.postValue(userlist)
        }
    }

    fun addFriend(userId: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val userid = userDao.getUserInfo()?.uid
                if (userid != null) {
                    auth.requestFriend(userId, userid)
                    _friendsList.postValue(auth.fetchUserFriends() as MutableList<UserFriends>?)

                }
            }
        }
    }

    fun clearList() {
        _usersList.postValue(mutableListOf())
    }

    fun switchSearchState() {
        _searchState.postValue(!_searchState.value!!)
    }

}

