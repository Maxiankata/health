package com.example.healthtracker

import android.graphics.Bitmap
import com.example.healthtracker.data.user.UserInfo
import com.example.healthtracker.data.user.UserMegaInfo
import kotlinx.coroutines.flow.Flow

interface AuthInterface {
    fun setUser(email: String, username: String, uid: String)

    suspend fun signOut()
    suspend fun getCurrentUser(): UserInfo?
    suspend fun getEntireUser(): Flow<UserMegaInfo?>
    suspend fun getUserInfo(uid: String): UserInfo?
    suspend fun createAcc(email: String, password: String, username: String)
    suspend fun addFriend(userId: String)
    suspend fun fetchUserFriends(): List<UserInfo>?
    suspend fun removeFriend(userId: String, userFriendList: List<UserInfo>)
    suspend fun fetchAllUsersInfo(): MutableList<UserInfo>
    suspend fun saveBitmapToDatabase(bitmap: Bitmap)
}