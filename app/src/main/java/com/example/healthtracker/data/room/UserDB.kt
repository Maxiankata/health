package com.example.healthtracker.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.healthtracker.data.GsonTypeConverter

@Database(entities = [UserData::class], version = 3)
@TypeConverters(GsonTypeConverter::class)
abstract class UserDB :RoomDatabase(){
    abstract fun dao(): UserDao
}