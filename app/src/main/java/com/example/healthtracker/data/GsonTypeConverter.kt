package com.example.healthtracker.data

import androidx.room.TypeConverter
import com.example.healthtracker.data.user.UserInfo
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class GsonTypeConverter {

        private val gson = Gson()

        @TypeConverter
        fun fromJson(json: String?): List<UserInfo>? {
            if (json == null) {
                return null
            }
            val type = object : TypeToken<List<UserInfo>>() {}.type
            return gson.fromJson(json, type)
        }

        @TypeConverter
        fun toJson(list: List<UserInfo>?): String? {
            return gson.toJson(list)
        }
}