package com.example.healthtracker

import android.graphics.Bitmap
import android.util.Log
import com.example.healthtracker.data.user.UserInfo
import com.example.healthtracker.data.user.UserMegaInfo
import com.example.healthtracker.ui.bitmapToBase64
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class AuthImpl : AuthInterface {
    override fun setUser(email: String, username: String, uid: String) {
        Firebase.database.getReference("user/${Firebase.auth.uid}").setValue(
            UserMegaInfo(
                UserInfo(
                    username, uid, "", email
                )
            )
        )
    }

    override suspend fun signOut() {
        withContext(Dispatchers.IO) {
            Firebase.auth.signOut()
        }
    }

    override suspend fun createAcc(email: String, password: String, username: String) {
        withContext(Dispatchers.IO) {
            Firebase.auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = Firebase.auth.currentUser
                        setUser(email, username, user!!.uid)
                    }
                }
        }
    }
    override suspend fun getEntireUser(): UserMegaInfo? {
        return try {
            val userPhotoRef =
                Firebase.database.getReference("user/${Firebase.auth.currentUser!!.uid}")
            val dataSnapshot = userPhotoRef.get().await()
            dataSnapshot.getValue(UserMegaInfo::class.java)
        } catch (e: Exception) {
            null
        }
    }
    override suspend fun getCurrentUser(): UserInfo? {
        return try {
            val userPhotoRef =
                Firebase.database.getReference("user/${Firebase.auth.currentUser!!.uid}/userInfo")
            val dataSnapshot = userPhotoRef.get().await()
            dataSnapshot.getValue(UserInfo::class.java)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun addFriend(userId: String) {
        Firebase.database.reference.child("user").child(Firebase.auth.currentUser!!.uid)
            .child("userFriends").child(userId).setValue(userId)
    }

    override suspend fun getUserInfo(uid: String): UserInfo? {
        return try {
            val userInfoRef = Firebase.database.getReference("user/$uid/userInfo")
            val dataSnapshot = userInfoRef.get().await()
            dataSnapshot.getValue(UserInfo::class.java)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun fetchUserFriends(): List<UserInfo> {
        return withContext(Dispatchers.IO) {
            val userFriendsRef =
                Firebase.database.getReference("user/${Firebase.auth.currentUser!!.uid}/userFriends")
            val dataSnapshot = userFriendsRef.get().await()
            val userFriendList = mutableListOf<UserInfo>()
            for (friendSnapshot in dataSnapshot.children) {
                val friendUid = friendSnapshot.key.toString()
                val friendInfo = getUserInfo(friendUid)
                friendInfo?.let {
                    userFriendList.add(it)
                    Log.d("USER LIST POST ADD", userFriendList.toString())
                }
            }
            Log.d("READY FOR RETURN USER LIST", userFriendList.toString())
            userFriendList
        }

    }

    override suspend fun removeFriend(userId: String, userFriendList: List<UserInfo>) {
        val friend = getUserInfo(userId)
        if (userFriendList.contains(friend)) {
            Firebase.database.reference.child("user").child(Firebase.auth.currentUser!!.uid)
                .child("userFriends").child(userId).removeValue()
        }
    }

    companion object {
        @Volatile
        private var instance: AuthImpl? = null
        fun getInstance() = instance ?: synchronized(this) {
            instance ?: AuthImpl().also { instance = it }
        }
    }
    override suspend fun saveBitmapToDatabase(bitmap: Bitmap) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val database: DatabaseReference =
            Firebase.database.getReference("user/${currentUser!!.uid}/userInfo/image")
        val base64String = bitmapToBase64(bitmap)
        withContext(Dispatchers.IO) {
            database.setValue(base64String).await()
        }
    }
    override suspend fun fetchAllUsersInfo(): MutableList<UserInfo> =
        suspendCoroutine { continuation ->
            val ref: DatabaseReference = Firebase.database.getReference("user")
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val tempList = mutableListOf<UserInfo>()
                    for (userSnapshot in dataSnapshot.children) {
                        val userInfoSnapshot = userSnapshot.child("userInfo")
                        val userInfo = userInfoSnapshot.getValue(UserInfo::class.java)
                        userInfo?.let {
                            if (it.uid != Firebase.auth.currentUser!!.uid){
                                tempList.add(it)
                            }
                        }
                    }
                    continuation.resume(tempList)
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("FirebaseError", "Error fetching data", databaseError.toException())
                    continuation.resumeWithException(databaseError.toException())
                }
            })
        }
}

