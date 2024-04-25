package com.example.healthtracker

import android.graphics.Bitmap
import android.util.Log
import com.example.healthtracker.data.user.UserDays
import com.example.healthtracker.data.user.UserFriends
import com.example.healthtracker.data.user.UserInfo
import com.example.healthtracker.data.user.UserMegaInfo
import com.example.healthtracker.data.user.UserSettingsInfo
import com.example.healthtracker.ui.account.friends.challenges.Challenge
import com.example.healthtracker.ui.bitmapToBase64
import com.example.healthtracker.ui.home.speeder.ActivityEnum
import com.example.healthtracker.ui.toUserDays
import com.example.healthtracker.ui.toUserFriends
import com.example.healthtracker.ui.toUserInfo
import com.example.healthtracker.ui.toUserMegaInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class AuthImpl : AuthInterface {
    override suspend fun setUser(email: String, username: String, uid: String) {
        Firebase.database.getReference("user/${Firebase.auth.currentUser?.uid}").setValue(
            UserMegaInfo(
                UserInfo(
                    username, uid, "", email
                )
            )
        )
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

    override suspend fun updateUserInfo(userInfo: UserInfo) {
        Firebase.database.reference.child("user/${userInfo.uid}/userInfo").setValue(userInfo)
    }

    override suspend fun updateSettings(userSettingsInfo: UserSettingsInfo) {
        Firebase.database.reference.child("user/${Firebase.auth.currentUser?.uid}/userSettingsInfo")
            .setValue(userSettingsInfo)
    }

    override suspend fun deleteCurrentUser() {
        Firebase.database.getReference("user/${Firebase.auth.currentUser?.uid}").setValue(null)
        Firebase.auth.currentUser?.delete()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                signOut()
            }
        }
    }


    override suspend fun checkCurrentUser() = Firebase.auth.currentUser != null

    override suspend fun logIn(email: String, password: String): Boolean {
        return try {
            Firebase.auth.signInWithEmailAndPassword(email, password).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun signOut() {
        Firebase.auth.signOut()
    }

    private val customCoroutineScope = CoroutineScope(Dispatchers.IO)

    override suspend fun createAcc(email: String, password: String, username: String): Boolean {
        return try {
            val authResult = Firebase.auth.createUserWithEmailAndPassword(email, password).await()
            val user = authResult.user
            if (user != null) {
                val userRef = Firebase.database.getReference("user")
                val userer = UserMegaInfo(
                    UserInfo(
                        username = username, mail = email, uid = user.uid
                    )
                )
                userRef.child(user.uid).setValue(userer).await()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun fetchSearchedUsers(string: String): MutableList<UserInfo> =
        suspendCoroutine { continuation ->
            val ref: DatabaseReference = Firebase.database.getReference("user")
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val tempList = mutableListOf<UserInfo>()
                    for (userSnapshot in dataSnapshot.children) {
                        val userInfoSnapshot = userSnapshot.child("userInfo")
                        val userInfo = userInfoSnapshot.toUserInfo()
                        userInfo.let {
                            if (it.uid != Firebase.auth.currentUser!!.uid) {
                                if (it.username?.lowercase() == string.lowercase()) {
                                    tempList.add(it)
                                }
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

    override suspend fun fetchSearchedFriends(string: String): MutableList<UserInfo> =
        suspendCoroutine { continuation ->
            val ref: DatabaseReference =
                Firebase.database.getReference("user/${Firebase.auth.currentUser!!.uid}/userFriends")
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val tempList = mutableListOf<UserInfo>()
                    for (userSnapshot in dataSnapshot.children) {
                        val userFriend = userSnapshot.toUserFriends()
                        if (userFriend.uid != Firebase.auth.currentUser!!.uid) {
                            CoroutineScope(Dispatchers.IO).launch {
                                val user = getUserInfo(userFriend.uid)
                                user?.let {
                                    if (string.lowercase() == user.username?.lowercase()) {
                                        tempList.add(user)
                                    }
                                }
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

    override suspend fun sync(megaInfo: UserMegaInfo, id: String) {
        Firebase.database.reference.child("user/${id}/userInfo").setValue(megaInfo.userInfo)
        Firebase.database.reference.child("user/${id}/userAutomaticInfo")
            .setValue(megaInfo.userAutomaticInfo)
        Firebase.database.reference.child("user/${id}/userPutInInfo")
            .setValue(megaInfo.userPutInInfo)
        Firebase.database.reference.child("user/${id}/userSettingsInfo")
            .setValue(megaInfo.userSettingsInfo)
        Firebase.database.reference.child("user/${id}/challenges").setValue(megaInfo.challenges)
        Firebase.database.reference.child("user/${id}/userDays").setValue(megaInfo.userDays)
    }

    override suspend fun getEntireUser(): Flow<UserMegaInfo?> = flow {
        Firebase.auth.currentUser?.uid?.let { uid ->
            val user = Firebase.database.getReference("user/$uid").get().await()
            emit(user.toUserMegaInfo())
        }
    }

    override suspend fun clearChallenges() {
        Firebase.database.reference.child("user/${Firebase.auth.currentUser?.uid}/challenges")
            .setValue(null)
    }


    override suspend fun fetchFriendDays(id: String): List<UserDays> {
        return withContext(Dispatchers.IO) {
            val userDaysRef = Firebase.database.getReference("user/$id/userDays")
            val dataSnapshot = userDaysRef.get().await()
            val userDaysList = mutableListOf<UserDays>()
            for (userDaysSnapshot in dataSnapshot.children) {
                val userDays = userDaysSnapshot.toUserDays()
                userDays.let {
                    userDaysList.add(it)
                }
            }
            userDaysList
        }
    }

    override suspend fun getCurrentUser(): UserMegaInfo? {
        return try {
            val userRef = Firebase.database.getReference("user/${Firebase.auth.currentUser!!.uid}")
            val dataSnapshot = userRef.get().await()
            dataSnapshot.getValue(UserMegaInfo::class.java)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun requestFriend(sentToUser: String, sentByUser: String) {
        Firebase.database.reference.child("user").child(sentToUser).child("userFriends")
            .child(sentByUser).setValue(UserFriends(sentByUser, false))
    }

    override suspend fun addFriend(sentToUser: String, sentByUser: String) {
        Firebase.database.reference.child("user").child(sentByUser).child("userFriends")
            .child(sentToUser).setValue(UserFriends(sentToUser, true))

        Firebase.database.reference.child("user").child(sentToUser).child("userFriends")
            .child(sentByUser).setValue(UserFriends(sentByUser, true))
    }

    override suspend fun removeFriend(userId: String, userFriendList: List<UserFriends>) {
        Firebase.database.reference.child("user").child(Firebase.auth.currentUser!!.uid)
            .child("userFriends").child(userId).removeValue()

        Firebase.database.reference.child("user").child(userId).child("userFriends")
            .child(Firebase.auth.currentUser!!.uid).removeValue()
    }

    override suspend fun fetchUserFriends(): List<UserFriends> {
        return withContext(Dispatchers.IO) {
            val userFriendsRef =
                Firebase.database.getReference("user/${Firebase.auth.currentUser!!.uid}/userFriends")
            val dataSnapshot = userFriendsRef.get().await()
            val userFriendList = mutableListOf<UserFriends>()
            for (friendSnapshot in dataSnapshot.children) {
                val friend = friendSnapshot.toUserFriends()
                val status = friend.isFriend
                val id = friend.uid
                userFriendList.add(UserFriends(id, status))
            }
            userFriendList
        }

    }

    override suspend fun fetchOwnChallenges(): List<Challenge>? {
        return withContext(Dispatchers.IO) {
            try {
                val challengesRef =
                    Firebase.database.getReference("user/${Firebase.auth.currentUser!!.uid}/challenges")
                val dataSnapshot = challengesRef.get().await()
                val challengeList = mutableListOf<Challenge>()
                for (challengeSnapshot in dataSnapshot.children) {
                    val assigner = challengeSnapshot.child("assigner").getValue(String::class.java)
                    val image = challengeSnapshot.child("image").getValue(String::class.java)
                    val challengeType = challengeSnapshot.child("challengeType").getValue(
                        ActivityEnum::class.java
                    )
                    val id = challengeSnapshot.child("id").getValue(Int::class.java)
                    val challengeDuration =
                        challengeSnapshot.child("challengeDuration").getValue(String::class.java)
                    val challengeCompletion =
                        challengeSnapshot.child("challengeCompletion").getValue(Boolean::class.java)
                    if (id != null && assigner != null && challengeType != null && challengeDuration != null && challengeCompletion != null && image != null) {
                        val challenge = Challenge(
                            id,
                            image,
                            assigner,
                            challengeType,
                            challengeDuration,
                            challengeCompletion,
                        )
                        challengeList.add(challenge)
                    }
                }
                challengeList
            } catch (e: Exception) {
                Log.e("Fetch Challenges", "Error fetching challenges", e)
                null
            }
        }
    }

    override suspend fun fetchChallenges(userId: String): List<Challenge>? {
        return withContext(Dispatchers.IO) {
            val challengesRef = Firebase.database.getReference("user/${userId}/challenges")
            try {
                val dataSnapshot = challengesRef.get().await()
                val challengeList = mutableListOf<Challenge>()
                for (challengeSnapshot in dataSnapshot.children) {
                    val assigner = challengeSnapshot.child("assigner").getValue(String::class.java)
                    val image = challengeSnapshot.child("image").getValue(String::class.java)
                    val challengeType = challengeSnapshot.child("challengeType").getValue(
                        ActivityEnum::class.java
                    )
                    val id = challengeSnapshot.child("id").getValue(Int::class.java)
                    val challengeDuration =
                        challengeSnapshot.child("challengeDuration").getValue(String::class.java)
                    val challengeCompletion =
                        challengeSnapshot.child("challengeCompletion").getValue(Boolean::class.java)
                    if (id != null && image != null && assigner != null && challengeType != null && challengeDuration != null && challengeCompletion != null) {
                        val challenge = Challenge(
                            id,
                            image,
                            assigner,
                            challengeType,
                            challengeDuration,
                            challengeCompletion
                        )
                        challengeList.add(challenge)
                    } else {
                    }
                }
                challengeList
            } catch (e: Exception) {
                null
            }
        }
    }

    override suspend fun setChallenges(challenges: List<Challenge>, userId: String) {
        Firebase.database.reference.child("user/${userId}/challenges").setValue(challenges)
    }

    override suspend fun resetPassword(email: String): Boolean {
        return suspendCoroutine { continuation ->
            Firebase.auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
                val isSuccessful = task.isSuccessful
                continuation.resume(isSuccessful)
            }
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

