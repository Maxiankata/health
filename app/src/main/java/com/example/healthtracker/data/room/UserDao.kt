package com.example.healthtracker.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.healthtracker.data.user.UserAutomaticInfo
import com.example.healthtracker.data.user.UserDays
import com.example.healthtracker.data.user.UserInfo
import com.example.healthtracker.data.user.UserPutInInfo
import com.example.healthtracker.data.user.UserSettingsInfo

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveUser(userMegaInfoEntity: UserData)
//    @Query("SELECT userAutomaticInfo, userPutInInfo FROM UserData WHERE userId=:userId")
//    fun getStatistics(userId: Int): Pair<List<UserAutomaticInfo>, List<UserPutInInfo>>?

    @Query("SELECT * FROM UserData")
    fun getEntireUser(): UserData?
    @Query("DELETE FROM UserData")
    fun dropUser()
    @Query("SELECT userInfo FROM UserData WHERE userId=:userId")
    fun getBasicInfo(userId: String): UserInfo?

    @Query("SELECT userSettingsInfo FROM UserData WHERE userId=:userId")
    fun getUserSettings(userId: String): UserSettingsInfo?

    @Query("SELECT userAutomaticInfo FROM UserData")
    fun getAutomaticInfo(): UserAutomaticInfo?

    @Query("SELECT userPutInInfo FROM UserData")
    fun getPutInInfo(): UserPutInInfo?

    @Query("UPDATE UserData SET userPutInInfo = :updatedUserPutInInfo")
    fun updateUserPutInInfo(updatedUserPutInInfo: UserPutInInfo)
    @Query("UPDATE UserData SET userAutomaticInfo = :updatedUserAutomaticInfo")
    fun updateUserAutomaticInfo(updatedUserAutomaticInfo: UserAutomaticInfo)
    @Query("UPDATE UserData SET UserInfo = :updatedUserInfo")
    suspend fun updateImage( updatedUserInfo: UserInfo)

    @Query("UPDATE UserData SET userAutomaticInfo = NULL")
    suspend fun wipeUserAutomaticInfo()
    @Query("UPDATE UserData SET userAutomaticInfo = NULL")
    suspend fun wipeUserPutInInfo()
    @Query ("UPDATE UserData SET userDays= :updatedUserDays")
    suspend fun updateDays(updatedUserDays: List<UserDays>)
    @Query ("UPDATE UserData SET userFriends = :updatedUserFriends")
    suspend fun addFriend(updatedUserFriends:List<UserInfo>)
}
