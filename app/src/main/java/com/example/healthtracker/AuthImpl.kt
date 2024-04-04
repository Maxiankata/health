package com.example.healthtracker

import android.graphics.Bitmap
import android.util.Log
import com.example.healthtracker.data.user.UserDays
import com.example.healthtracker.data.user.UserInfo
import com.example.healthtracker.data.user.UserMegaInfo
import com.example.healthtracker.ui.account.friends.challenges.Challenge
import com.example.healthtracker.ui.account.friends.challenges.ChallengeType
import com.example.healthtracker.ui.bitmapToBase64
import com.example.healthtracker.ui.toUserDays
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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

    override suspend fun createAcc(email: String, password: String, username: String): String? {
        return withContext(Dispatchers.IO) {
            var uidReturn: String? = null
            try {
                suspendCoroutine { continuation ->
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

    override suspend fun fetchSearchedFriends(string: String): MutableList<UserInfo> =
        suspendCoroutine { continuation ->
            val ref = Firebase.database.getReference("user/${Firebase.auth.currentUser!!.uid}")
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val tempList = mutableListOf<UserInfo>()
                    for (userSnapshot in dataSnapshot.children) {
                        val userInfoSnapshot = userSnapshot.child("UserFriends")
                        val userInfo = userInfoSnapshot.getValue(UserInfo::class.java)
                        userInfo?.let {
                            if (it.username == string) {
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

    override suspend fun fetchSearchedUsers(string: String): MutableList<UserInfo> =
        suspendCoroutine { continuation ->
            val ref: DatabaseReference = Firebase.database.getReference("user")
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val tempList = mutableListOf<UserInfo>()
                    for (userSnapshot in dataSnapshot.children) {
                        val userInfoSnapshot = userSnapshot.child("userInfo")
                        val userInfo = userInfoSnapshot.getValue(UserInfo::class.java)
                        userInfo?.let {
                            if (it.username == string) {
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

    override suspend fun sync(megaInfo: UserMegaInfo, id: String) {
        Firebase.database.reference.child("user/${id}/userInfo")
            .setValue(megaInfo.userInfo)
        Firebase.database.reference.child("user/${id}/userAutomaticInfo")
            .setValue(megaInfo.userAutomaticInfo)
        Firebase.database.reference.child("user/${id}/userPutInInfo")
            .setValue(megaInfo.userPutInInfo)
        Firebase.database.reference.child("user/${id}/userSettingsInfo")
            .setValue(megaInfo.userSettingsInfo)
        Firebase.database.reference.child("user/${id}/userDays")
            .setValue(megaInfo.userDays)
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

    override suspend fun fetchFriendDays(id: String): List<UserDays>? {
        return withContext(Dispatchers.IO) {
            val userDaysRef = Firebase.database.getReference("user/$id/userDays")
            val dataSnapshot = userDaysRef.get().await()
            val userDaysList = mutableListOf<UserDays>()
            for (userDaysSnapshot in dataSnapshot.children) {
                Log.d("Entering snapshot", "")
                val userDays = userDaysSnapshot.toUserDays()
                userDays.let {
                    userDaysList.add(it)
                    Log.d("Adding userDays", it.toString())
                }.run {
                    Log.d("Snapshot is null", "")
                }
            }
            userDaysList
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

    override suspend fun fetchOwnChallenges(): List<Challenge>? {
        return withContext(Dispatchers.IO) {
            try {
                val challengesRef =
                    Firebase.database.getReference("user/${Firebase.auth.currentUser!!.uid}/challenges")
                val dataSnapshot = challengesRef.get().await()
                val challengeList = mutableListOf<Challenge>()
                for (challengeSnapshot in dataSnapshot.children) {
                    val assigner = challengeSnapshot.child("assigner").getValue(String::class.java)
                    val challengeType = challengeSnapshot.child("challengeType").getValue(
                        ChallengeType::class.java
                    )
                    val challengeDuration =
                        challengeSnapshot.child("challengeDuration").getValue(String::class.java)
                    val challengeCompletion =
                        challengeSnapshot.child("challengeCompletion").getValue(Boolean::class.java)
                    if (assigner != null && challengeType != null && challengeDuration != null && challengeCompletion != null) {
                        val challenge = Challenge(
                            assigner,
                            challengeType,
                            challengeDuration,
                            challengeCompletion
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
            val challengesRef =
                Firebase.database.getReference("user/${userId}/challenges")
            try {
                val dataSnapshot = challengesRef.get().await()
                val challengeList = mutableListOf<Challenge>()
                for (challengeSnapshot in dataSnapshot.children) {
                    val assigner = challengeSnapshot.child("assigner").getValue(String::class.java)
                    val challengeType = challengeSnapshot.child("challengeType").getValue(
                        ChallengeType::class.java
                    )
                    val challengeDuration =
                        challengeSnapshot.child("challengeDuration").getValue(String::class.java)
                    val challengeCompletion =
                        challengeSnapshot.child("challengeCompletion").getValue(Boolean::class.java)
                    if (assigner != null && challengeType != null && challengeDuration != null && challengeCompletion != null) {
                        val challenge = Challenge(
                            assigner,
                            challengeType,
                            challengeDuration,
                            challengeCompletion
                        )
                        challengeList.add(challenge)
                        Log.d("Adding challenge", challenge.toString())
                    } else {
                        Log.e("Fetch Challenges", "Invalid data for challenge: $challengeSnapshot")
                    }
                }
                challengeList
            } catch (e: Exception) {
                Log.e("Fetch Challenges", "Error fetching challenges", e)
                null
            }
        }
    }

    override suspend fun setChallenges(challenges: List<Challenge>, userId: String) {
        Firebase.database.reference.child("user/${userId}/challenges")
            .setValue(challenges)
    }

    override suspend fun resetPassword(email: String): Boolean {
        return suspendCoroutine { continuation ->
            Firebase.auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    val isSuccessful = task.isSuccessful
                    continuation.resume(isSuccessful)
                }
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

    override suspend fun deleteCurrentUser() {
            Firebase.auth.currentUser?.delete()?.addOnCompleteListener {task->
                if (task.isSuccessful){
                        signOut()
                }
            }
    }

}

