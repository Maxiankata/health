package com.example.healthtracker.room_data
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.healthtracker.user.UserAutomaticInfo
import com.example.healthtracker.user.UserInfo
import com.example.healthtracker.user.UserPutInInfo
import com.example.healthtracker.user.UserSettingsInfo

@Entity
data class UserData(
@PrimaryKey val userId:Int,
@ColumnInfo (name = "userInfo") val userInfo: UserInfo?,
@ColumnInfo(name = "userSettingsInfo") val userSettingsInfo: UserSettingsInfo?,
@ColumnInfo(name = "userAutomaticInfo") val userAutomaticInfo: UserAutomaticInfo?,
@ColumnInfo(name = "userPutInInfo") val userPutInInfo: UserPutInInfo?,
@ColumnInfo(name = "userFriends") val userFriends: List<UserInfo>?
)

