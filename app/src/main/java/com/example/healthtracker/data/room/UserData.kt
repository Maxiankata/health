package com.example.healthtracker.data.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.healthtracker.ui.account.friends.challenges.Challenge
import com.example.healthtracker.data.GsonTypeConverter
import com.example.healthtracker.data.user.Achievement
import com.example.healthtracker.data.user.UserAutomaticInfo
import com.example.healthtracker.data.user.UserDays
import com.example.healthtracker.data.user.UserInfo
import com.example.healthtracker.data.user.UserPutInInfo
import com.example.healthtracker.data.user.UserSettingsInfo

@Entity
data class UserData(
    @PrimaryKey val userId: String,
    @ColumnInfo(name = "userInfo") val userInfo: UserInfo,
    @ColumnInfo(name = "userSettingsInfo") val userSettingsInfo: UserSettingsInfo?,
    @ColumnInfo(name = "userAutomaticInfo") val userAutomaticInfo: UserAutomaticInfo?,
    @ColumnInfo(name = "userPutInInfo") val userPutInInfo: UserPutInInfo?,
    @ColumnInfo(name = "userFriends") val userFriends: List<UserInfo>?,
    @ColumnInfo(name = "userDays")
    @TypeConverters(GsonTypeConverter::class)
    val userDays:List<UserDays>?,
    @ColumnInfo(name = "challenges") val challenges:List<Challenge>?,
    @ColumnInfo(name = "achievements") val achievements:List<Achievement>?
)

