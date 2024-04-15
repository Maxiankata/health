package com.example.healthtracker.data

import androidx.room.TypeConverter
import com.example.healthtracker.data.user.Achievement
import com.example.healthtracker.data.user.StepsInfo
import com.example.healthtracker.data.user.UserAutomaticInfo
import com.example.healthtracker.data.user.UserDays
import com.example.healthtracker.data.user.UserFriends
import com.example.healthtracker.data.user.UserGoals
import com.example.healthtracker.data.user.UserInfo
import com.example.healthtracker.data.user.UserMegaInfo
import com.example.healthtracker.data.user.UserPutInInfo
import com.example.healthtracker.data.user.UserSettingsInfo
import com.example.healthtracker.data.user.WaterInfo
import com.example.healthtracker.ui.account.friends.challenges.Challenge
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Calendar

class GsonTypeConverter {

    private val gson = Gson()

    @TypeConverter
    fun fromUserMegaInfo(userMegaInfo: UserMegaInfo?): String? {
        return gson.toJson(userMegaInfo)
    }
    @TypeConverter
    fun toUserMegaInfo(userMegaInfoString: String?): UserMegaInfo? {
        return gson.fromJson(userMegaInfoString, UserMegaInfo::class.java)
    }

    @TypeConverter
    fun fromUserInfo(userInfo: UserInfo?): String? {
        return gson.toJson(userInfo)
    }

    @TypeConverter
    fun toUserInfo(userInfoString: String?): UserInfo? {
        return gson.fromJson(userInfoString, UserInfo::class.java)
    }

    @TypeConverter
    fun fromUserAutomaticInfo(userAutomaticInfo: UserAutomaticInfo?): String? {
        return gson.toJson(userAutomaticInfo)
    }

    @TypeConverter
    fun toUserAutomaticInfo(userAutomaticInfoString: String?): UserAutomaticInfo? {
        return gson.fromJson(userAutomaticInfoString, UserAutomaticInfo::class.java)
    }
    @TypeConverter
    fun fromUserFriends(userFriends: UserFriends?): String? {
        return gson.toJson(userFriends)
    }

    @TypeConverter
    fun toUserFriends(userFriends: String?): UserFriends? {
        return gson.fromJson(userFriends, UserFriends::class.java)
    }


    @TypeConverter
    fun fromUserPutInInfo(userPutInInfo: UserPutInInfo?): String? {
        return gson.toJson(userPutInInfo)
    }

    @TypeConverter
    fun toUserPutInInfo(userPutInInfoString: String?): UserPutInInfo? {
        return gson.fromJson(userPutInInfoString, UserPutInInfo::class.java)
    }
    @TypeConverter
    fun fromUserGoals(userGoals: UserGoals?): String? {
        return gson.toJson(userGoals)
    }

    @TypeConverter
    fun toUserGoals(userGoalsString: String?): UserGoals? {
        return gson.fromJson(userGoalsString, UserGoals::class.java)
    }
    @TypeConverter
    fun fromWaterInfo(waterInfo: WaterInfo?): String? {
        return gson.toJson(waterInfo)
    }

    @TypeConverter
    fun toWaterInfo(waterInfoString: String?): WaterInfo? {
        return gson.fromJson(waterInfoString, WaterInfo::class.java)
    }

    @TypeConverter
    fun fromStepsInfo(stepsInfo: StepsInfo?): String? {
        return gson.toJson(stepsInfo)
    }

    @TypeConverter
    fun toStepsInfo(stepsInfoString: String?): StepsInfo? {
        return gson.fromJson(stepsInfoString, StepsInfo::class.java)
    }

    @TypeConverter
    fun fromUserSettingsInfo(userSettingsInfo: UserSettingsInfo?): String? {
        return gson.toJson(userSettingsInfo)
    }
    @TypeConverter
    fun fromAchievements(achievements: Achievement?): String? {
        return gson.toJson(achievements)
    }
    @TypeConverter
    fun toAchievements(achievements: String?): Achievement? {
        return gson.fromJson(achievements, Achievement::class.java)
    }

    @TypeConverter
    fun toUserSettingsInfo(userSettingsInfoString: String?): UserSettingsInfo? {
        return gson.fromJson(userSettingsInfoString, UserSettingsInfo::class.java)
    }

    @TypeConverter
    fun fromUserInfoList(userInfoList: List<UserInfo>?): String? {
        return gson.toJson(userInfoList)
    }
    @TypeConverter
    fun fromUserFriendsList(userFriendsList: List<UserFriends>?): String? {
        return gson.toJson(userFriendsList)
    }
    @TypeConverter
    fun toUserFriendsList(userFriendsList: String?): List<UserFriends>? {
        val listType = object : TypeToken<List<UserFriends>>() {}.type
        return gson.fromJson(userFriendsList, listType)
    }
    @TypeConverter
    fun toUserInfoList(userInfoListString: String?): List<UserInfo>? {
        val listType = object : TypeToken<List<UserInfo>>() {}.type
        return gson.fromJson(userInfoListString, listType)
    }
    @TypeConverter
    fun fromChallengesList(challengeList: List<Challenge>?): String? {
        return gson.toJson(challengeList)
    }

    @TypeConverter
    fun toChallengeList(challengeList: String?): List<Challenge>? {
        val listType = object : TypeToken<List<Challenge>>() {}.type
        return gson.fromJson(challengeList, listType)
    }
    private val type = object : TypeToken<List<UserDays>>() {}.type

    @TypeConverter
    fun fromString(userDaysListString: String?): List<UserDays>? {
        return gson.fromJson(userDaysListString, type)
    }

    @TypeConverter
    fun toString(userDaysList: List<UserDays>?): String? {
        return gson.toJson(userDaysList)
    }
    @TypeConverter
    fun fromAchievementsList(achievementList: List<Achievement>?): String? {
        return gson.toJson(achievementList)
    }

    @TypeConverter
    fun toAchievementList(achievementList: String?): List<Achievement>? {
        val listType = object : TypeToken<List<Achievement>>() {}.type
        return gson.fromJson(achievementList, listType)
    }
    @TypeConverter
    fun calendarToJson(calendar: Calendar?): String? = Gson().toJson(calendar)

    @TypeConverter
    fun jsonToCalendar(json: String?): Calendar? = Gson().fromJson(json, Calendar::class.java)

}