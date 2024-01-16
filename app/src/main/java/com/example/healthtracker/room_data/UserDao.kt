package com.example.healthtracker.room_data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.healthtracker.user.StepsInfo
import com.example.healthtracker.user.UserAutomaticInfo
import com.example.healthtracker.user.UserInfo
import com.example.healthtracker.user.UserMegaInfo
import com.example.healthtracker.user.UserPutInInfo
import com.example.healthtracker.user.UserSettingsInfo

@Dao
interface UserDao {
    @Upsert
    fun saveUser(UserMegaInfo: UserMegaInfo)


    @Query("SELECT userFriends FROM UserData WHERE userId=:userId")
    fun getAllFriends(userId:Int): List<UserInfo>?=null

    @Query("Select userAutomaticInfo AND userPutInInfo from UserData WHERE userId=:userId")
    fun getStatistics(userId:Int): Pair<List<UserAutomaticInfo>, List<UserPutInInfo>>?=null

    @Query("Select * FROM UserData WHERE userId=:userId")
    fun getEntireUser(userId:Int): UserMegaInfo?=null

    @Query("SELECT userInfo FROM UserData WHERE userId=:userId")
    fun getBasicInfo(userId:Int):UserInfo?=null

    @Query("SELECT userSettingsInfo FROM UserData WHERE userId=:userId")
    fun getUserSettings(userId:Int):UserSettingsInfo?=null

    @Query("SELECT userAutomaticInfo FROM UserData WHERE userId=:userId")
    fun getStepInfo(userId:Int):StepsInfo?=null
}