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
import kotlinx.coroutines.currentCoroutineContext
import kotlin.coroutines.coroutineContext

class FriendListViewModel : ViewModel() {
    private val _user = MutableLiveData<MutableList<UserInfo>?>()
    val user: MutableLiveData<MutableList<UserInfo>?> get() = _user
    private val database = FirebaseDatabase.getInstance()
    private var _searchState = MutableLiveData<Boolean>()
    lateinit var threat:Thread
    private lateinit var auth: FirebaseAuth

    val searchState: LiveData<Boolean> get() = _searchState
    init {
        _user.postValue(null)
        _searchState.value=true
        fetchAllUsersInfo()
        switchSearchState()
    }
    val ref: DatabaseReference = database.getReference("user")
    val userInfoList = mutableListOf<UserInfo>()
    fun fetchAllUsersInfo() {
        user.value?.clear()
        Thread {
            ref.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (userSnapshot in dataSnapshot.children) {
                        val userInfoSnapshot = userSnapshot.child("userInfo")
                        val userInfo = userInfoSnapshot.getValue(UserInfo::class.java)
                        userInfo?.let {
                            userInfoList.add(it)
                        }
                    }
                    _user.postValue(userInfoList)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("FirebaseError", "Error fetching data", databaseError.toException())
//                        threat.interrupt()
                }
            }
            )
        }.start()
    }
    fun addFriend(){
            val databaseReference: DatabaseReference = FirebaseDatabase.getInstance()
                .getReference("user/${LoginActivity.auth.currentUser!!.uid}/userFriendInfo/friends")
            databaseReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val friendsList: ArrayList<UserInfo> = ArrayList()
                        for (friendSnapshot in dataSnapshot.children) {
                            // Convert each child to a UserInfo object and add it to the list
                            val friendInfo: UserInfo? =
                                friendSnapshot.getValue(UserInfo::class.java)
                            friendInfo?.let { friendsList.add(it) }
                        }
                    } else {
                        Log.d("You have no friends", "LOL FRIENDLESS")
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            })
    }


    fun fetchUserFriends(){
        auth = FirebaseAuth.getInstance()
        val friends = Firebase.database.getReference("user/${auth.currentUser!!.uid}/userFriendInfo/friends").get()
        Thread {
            ref.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (userSnapshot in dataSnapshot.children) {
                        val userInfoSnapshot = userSnapshot.child("user/${auth.currentUser!!.uid}/userFriendInfo/friends")

                        val userInfo = userInfoSnapshot.getValue(UserInfo::class.java)
                        userInfo?.let {
                            userInfoList.add(it)
                        }
                    }
                    _user.postValue(userInfoList)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("FirebaseError", "Error fetching data", databaseError.toException())
//                        threat.interrupt()
                }
            }
            )
        }.start()
    }
    fun getUserInfoByUid(uid: String): UserInfo? {

        return userInfoList.find {  uid==it.uid }
    }
    fun switchSearchState() {
        _searchState.postValue(!_searchState.value!!)
    }

}

