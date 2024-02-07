package com.example.healthtracker.data.room

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.healthtracker.data.user.StepsInfo
import com.example.healthtracker.data.user.UserInfo
import com.example.healthtracker.data.user.UserPutInInfo
import com.example.healthtracker.data.user.UserSettingsInfo

@Dao
interface UserDao {
    @Upsert
    fun saveUser(userMegaInfoEntity: UserData)

//    @Query("SELECT userAutomaticInfo, userPutInInfo FROM UserData WHERE userId=:userId")
//    fun getStatistics(userId: Int): Pair<List<UserAutomaticInfo>, List<UserPutInInfo>>?

    @Query("SELECT * FROM UserData WHERE userId=:userId")
    fun getEntireUser(userId: String): UserData?

    @Query("SELECT userInfo FROM UserData WHERE userId=:userId")
    fun getBasicInfo(userId: String): UserInfo?

    @Query("SELECT userSettingsInfo FROM UserData WHERE userId=:userId")
    fun getUserSettings(userId: String): UserSettingsInfo?

    @Query("SELECT userAutomaticInfo FROM UserData WHERE userId=:userId")
    fun getStepInfo(userId: String): StepsInfo?

    @Query("SELECT userPutInInfo FROM UserData WHERE userId=:userId")
    fun getPutInInfo(userId: String): UserPutInInfo?

    @Query("UPDATE UserData SET userPutInInfo = :updatedUserPutInInfo WHERE userId = :userId")
    fun updateUserWaterInfo(userId: String, updatedUserPutInInfo: UserPutInInfo)
    @Query("UPDATE UserData SET UserInfo = :updatedUserInfo WHERE userId = :userId")
    fun updateImage(userId: String, updatedUserInfo: String)

}
