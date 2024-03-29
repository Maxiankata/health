package com.example.healthtracker

import android.graphics.Bitmap
import android.util.Log
import com.example.healthtracker.data.user.UserInfo
import com.example.healthtracker.data.user.UserMegaInfo
import com.example.healthtracker.ui.bitmapToBase64
import com.example.healthtracker.ui.toUserMegaInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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

    override suspend fun checkCurrentUser() = Firebase.auth.currentUser!=null

    override suspend fun logIn(email: String, password: String): Boolean {
        return try {
            Firebase.auth.signInWithEmailAndPassword(email, password).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun signOut() {
        withContext(Dispatchers.IO) {
            Firebase.auth.signOut()
        }
    }

    override suspend fun createAcc(email: String, password: String, username: String): String? {
        return withContext(Dispatchers.IO) {
            var uidReturn: String? = null
            try {
                suspendCoroutine{ continuation ->
                    Firebase.auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener {
                            val user = Firebase.auth.currentUser
                            setUser(email, username, user?.uid ?: "")
                            uidReturn = user?.uid
                            Log.d("REGISTERED USER ID", uidReturn.toString())
                            continuation.resume(Unit)
                        }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            Log.d("Returning id", uidReturn.toString())
            uidReturn
        }
    }

    override suspend fun sync(megaInfo: UserMegaInfo){
        Firebase.database.reference.child("user/${Firebase.auth.currentUser?.uid}").setValue(megaInfo)
    }

    override suspend fun getEntireUser(): Flow<UserMegaInfo?> = flow {
        Firebase.auth.currentUser?.uid?.let { uid ->
            val user = Firebase.database.getReference("user/$uid").get().await()
            emit(user.toUserMegaInfo())
        }
    }


    override suspend fun getCurrentUser(): UserMegaInfo? {
        return try {
            val userRef =
                Firebase.database.getReference("user/${Firebase.auth.currentUser!!.uid}")
            val dataSnapshot = userRef.get().await()
            dataSnapshot.getValue(UserMegaInfo::class.java)
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
                }
            }
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
                            if (it.uid != Firebase.auth.currentUser!!.uid) {
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

