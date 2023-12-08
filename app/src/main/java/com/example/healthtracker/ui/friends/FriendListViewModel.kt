package com.example.healthtracker.ui.friends

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.healthtracker.user.UserInfo
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FriendListViewModel : ViewModel() {
    private val _user = MutableLiveData<MutableList<UserInfo>>()
    val user: LiveData<MutableList<UserInfo>> get() = _user
    private val database = FirebaseDatabase.getInstance()

    init {
        fetchAllUsersInfo()
    }

    fun fetchAllUsersInfo() {
        val ref: DatabaseReference = database.getReference("user")
        val userInfoList = mutableListOf<UserInfo>() // List to hold user info

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (userSnapshot in dataSnapshot.children) {
                    val userInfoSnapshot = userSnapshot.child("userInfo")
                    val userInfo = userInfoSnapshot.getValue(UserInfo::class.java)
                    userInfo?.let {
                        userInfoList.add(it) // Collect userInfo for each user
                    }
                }

                _user.value = userInfoList.toMutableList() // Set LiveData value with a new list
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("FirebaseError", "Error fetching data", databaseError.toException())
            }
        })
    }
}

