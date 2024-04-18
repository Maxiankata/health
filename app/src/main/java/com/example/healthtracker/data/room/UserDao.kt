package com.example.healthtracker.data.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.healthtracker.ui.account.friends.challenges.Challenge
import com.example.healthtracker.data.user.UserAutomaticInfo
import com.example.healthtracker.data.user.UserDays
import com.example.healthtracker.data.user.UserInfo
import com.example.healthtracker.data.user.UserPutInInfo
import com.example.healthtracker.data.user.UserSettingsInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveUser(userMegaInfoEntity: UserData)
    @Query("SELECT * FROM UserData")
    fun getEntireUser(): UserData
    @Query("UPDATE UserData SET UserInfo = :updatedUserInfo")
    suspend fun updateUserInfo(updatedUserInfo: UserInfo)
    @Query("DELETE FROM UserData")
    fun dropUser()


    @Query("SELECT userInfo FROM UserData")
    fun getUserInfo(): UserInfo?


    @Query("SELECT userSettingsInfo FROM UserData ")
    fun getUserSettings(): UserSettingsInfo
    @Query("SELECT userSettingsInfo FROM UserData ")
    fun getUserSettingsLiveData(): LiveData<UserSettingsInfo>

    @Query("SELECT userAutomaticInfo FROM UserData")
    fun getAutomaticInfo(): UserAutomaticInfo?

    @Query("SELECT userPutInInfo FROM UserData")
    fun getPutInInfo(): UserPutInInfo?

    @Query("UPDATE UserData SET userPutInInfo = :updatedUserPutInInfo")
    fun updateUserPutInInfo(updatedUserPutInInfo: UserPutInInfo)
    @Query("UPDATE UserData SET userAutomaticInfo = :updatedUserAutomaticInfo")
    fun updateUserAutomaticInfo(updatedUserAutomaticInfo: UserAutomaticInfo)
    @Query("UPDATE UserData SET userSettingsInfo = :updatedUserSettingsInfo")
    fun updateUserSettings(updatedUserSettingsInfo: UserSettingsInfo)
    @Query("UPDATE UserData SET userAutomaticInfo = NULL")
    suspend fun wipeUserAutomaticInfo()
    @Query("UPDATE UserData SET userPutInInfo = NULL")
    suspend fun wipeUserPutInInfo()
    @Query("UPDATE UserData SET challenges = NULL")
    suspend fun wipeChallenges()
    @Query ("UPDATE UserData SET userDays= :updatedUserDays")
    suspend fun updateDays(updatedUserDays: List<UserDays>)
    @Query ("UPDATE UserData SET userFriends = :updatedUserFriends")
    suspend fun addFriend(updatedUserFriends:List<UserInfo>)
    @Query("UPDATE UserData SET challenges = :updatedChallenges")
    suspend fun updateChallenges(updatedChallenges: List<Challenge>)
    @Query("SELECT * FROM UserData")
    fun getEntireUserFlow(): Flow<UserData?>

}
