package com.example.healthtracker

import android.graphics.Bitmap
import com.example.healthtracker.data.user.UserDays
import com.example.healthtracker.ui.account.friends.challenges.Challenge
import com.example.healthtracker.data.user.UserInfo
import com.example.healthtracker.data.user.UserMegaInfo
import kotlinx.coroutines.flow.Flow

interface AuthInterface {
    suspend fun setUser(email: String, username: String, uid: String)

    fun signOut()
    suspend fun getCurrentUser(): UserMegaInfo?
    suspend fun getEntireUser(): Flow<UserMegaInfo?>
    suspend fun getUserInfo(uid: String): UserInfo?
    suspend fun createAcc(email: String, password: String, username: String):Boolean
    suspend fun logIn(email: String, password: String):Boolean?
    suspend fun checkCurrentUser():Boolean
    suspend fun updateUserInfo(userInfo: UserInfo)
    suspend fun sync(megaInfo: UserMegaInfo, id:String)
    suspend fun addFriend(userId: String)
    suspend fun resetPassword(email: String):Boolean
    suspend fun fetchUserFriends(): List<UserInfo>?
    suspend fun fetchChallenges(userId: String):List<Challenge>?
    suspend fun fetchOwnChallenges():List<Challenge>?
    suspend fun clearChallenges()
    suspend fun fetchFriendDays(id:String):List<UserDays>?
    suspend fun setChallenges(challenges: List<Challenge>, userId: String)
    suspend fun removeFriend(userId: String, userFriendList: List<UserInfo>)
    suspend fun fetchAllUsersInfo(): MutableList<UserInfo>
    suspend fun saveBitmapToDatabase(bitmap: Bitmap)
    suspend fun fetchSearchedUsers(string: String):List<UserInfo>
    suspend fun fetchSearchedFriends(string: String):List<UserInfo>
    suspend fun deleteCurrentUser()
}