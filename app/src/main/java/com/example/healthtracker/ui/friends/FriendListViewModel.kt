package com.example.healthtracker.ui.friends

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.healthtracker.ui.login.LoginActivity
import com.example.healthtracker.user.UserInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.coroutines.coroutineContext

class FriendListViewModel : ViewModel() {
    private val _user = MutableLiveData<MutableList<UserInfo>?>()
    val user: MutableLiveData<MutableList<UserInfo>?> get() = _user
    private val database = FirebaseDatabase.getInstance()
    private var _searchState = MutableLiveData<Boolean>()
    lateinit var threat:Thread
    private var auth: FirebaseAuth = LoginActivity.auth

    val searchState: LiveData<Boolean> get() = _searchState
    init {
        _user.postValue(null)
        _searchState.value=false

    }
    val ref: DatabaseReference = database.getReference("user")
    val ref2 = database.getReference("user/${auth.currentUser!!.uid}/userFriends")

    val allUsersInfoList = mutableListOf<UserInfo>()
    val friendsInfoList = mutableListOf<UserInfo>()

    fun fetchAllUsersInfo() {

            val eventer = ref.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    allUsersInfoList.clear()
                    for (userSnapshot in dataSnapshot.children) {
                        val userInfoSnapshot = userSnapshot.child("userInfo")
                        val userInfo = userInfoSnapshot.getValue(UserInfo::class.java)
                        userInfo?.let {
                            if (it.uid != auth.currentUser!!.uid){
                                allUsersInfoList.add(it)
                            }
                        }
                    }
                    _user.postValue(allUsersInfoList)
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("FirebaseError", "Error fetching data", databaseError.toException())
//                        threat.interrupt()
                }
            }
            )
        ref.addValueEventListener(eventer)
        ref.removeEventListener(eventer)
    }
    fun fetchUserFriends(){
            val eventer = ref2.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    friendsInfoList.clear()
                    for (snapshot in dataSnapshot.children) {
                        val userInfo = snapshot.value.toString()
                        userInfo.let {
                            val user = getUserInfoByUid(it)
                            if (user != null) {
                                friendsInfoList.add(user)
                            }
                        }
                    }
                    _user.postValue(friendsInfoList)
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("FirebaseError", "Error fetching data", databaseError.toException())
                }
            }
            )
        ref2.addValueEventListener(eventer)
        ref2.removeEventListener(eventer)

    }
    fun addFriend(userId: String) {
        val databases = Firebase.database.reference
        databases.child("user").child(auth.currentUser!!.uid).child("userFriends").child(userId).setValue(userId)
    }
    fun removeFriend(userId:String){
        val friend = getUserInfoByUid(userId)
        if (friendsInfoList.contains(friend)) {
            val databases = Firebase.database.reference
            databases.child("user").child(auth.currentUser!!.uid).child("userFriends").child(userId)
                .removeValue()
            fetchUserFriends()
        }
    }
    fun getUserInfoByUid(uid: String): UserInfo? {

        return allUsersInfoList.find {  uid==it.uid }
    }
    fun switchSearchState() {
        _searchState.postValue(!_searchState.value!!)
    }

}

