package com.example.healthtracker.room_data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.healthtracker.user.UserAutomaticInfo
import com.example.healthtracker.user.UserInfo
import com.example.healthtracker.user.UserMegaInfo
import com.example.healthtracker.user.UserPutInInfo
import com.example.healthtracker.user.UserSettingsInfo

@Dao
interface UserDao {
    @Upsert
    fun saveUser(UserMegaInfo: UserMegaInfo)

    @Query("SELECT userFriends FROM UserData")
    fun getAllFriends(): List<UserInfo>?=null

    @Query("Select userAutomaticInfo AND userPutInInfo from UserData")
    fun getStatistics(): Pair<List<UserAutomaticInfo>, List<UserPutInInfo>>?=null

    @Query("Select * FROM UserData")
    fun getWholeUser(): UserMegaInfo?=null

    @Query("SELECT userInfo FROM UserData")
    fun getBasicInfo():UserInfo?=null

    @Query("SELECT userSettingsInfo FROM UserData")
    fun getUserSettings():UserSettingsInfo?=null
}