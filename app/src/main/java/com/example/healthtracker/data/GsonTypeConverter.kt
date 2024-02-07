package com.example.healthtracker.data

import androidx.room.TypeConverter
import com.example.healthtracker.data.user.StepsInfo
import com.example.healthtracker.data.user.UserAutomaticInfo
import com.example.healthtracker.data.user.UserInfo
import com.example.healthtracker.data.user.UserMegaInfo
import com.example.healthtracker.data.user.UserPutInInfo
import com.example.healthtracker.data.user.UserSettingsInfo
import com.example.healthtracker.data.user.WaterInfo
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

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
    fun fromUserPutInInfo(userPutInInfo: UserPutInInfo?): String? {
        return gson.toJson(userPutInInfo)
    }

    @TypeConverter
    fun toUserPutInInfo(userPutInInfoString: String?): UserPutInInfo? {
        return gson.fromJson(userPutInInfoString, UserPutInInfo::class.java)
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
    fun toUserSettingsInfo(userSettingsInfoString: String?): UserSettingsInfo? {
        return gson.fromJson(userSettingsInfoString, UserSettingsInfo::class.java)
    }

    @TypeConverter
    fun fromUserInfoList(userInfoList: List<UserInfo>?): String? {
        return gson.toJson(userInfoList)
    }

    @TypeConverter
    fun toUserInfoList(userInfoListString: String?): List<UserInfo>? {
        val listType = object : TypeToken<List<UserInfo>>() {}.type
        return gson.fromJson(userInfoListString, listType)
    }
}