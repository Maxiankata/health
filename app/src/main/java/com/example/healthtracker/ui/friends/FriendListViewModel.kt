package com.example.healthtracker.ui.friends

import android.util.Log
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
                            allUsersInfoList.add(it)
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
            Log.d("REF2", ref2.toString())
            val eventer = ref2.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    friendsInfoList.clear()
                    for (snapshot in dataSnapshot.children) {
                        val userInfo = snapshot.value.toString()
                        userInfo.let {
                            val user = getUserInfoByUid(it)
                            Log.d("POST ADAPTION USERINFO", user.toString())
                            if (user != null) {
                                friendsInfoList.add(user)
                                Log.d("USER INFO LIST", friendsInfoList.toString())
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
//        databases.child("user").child("userFriends").push().setValue(userId)
        databases.child("user").child(auth.currentUser!!.uid).child("userFriends").child(userId).setValue(userId)
    }
    fun getUserInfoByUid(uid: String): UserInfo? {
        Log.d("GETTER ID", uid)
        Log.d("ALL USERS", allUsersInfoList.toString())

        return allUsersInfoList.find {  uid==it.uid }
    }
    fun switchSearchState() {
        _searchState.postValue(!_searchState.value!!)
    }

}

