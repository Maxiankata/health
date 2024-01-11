package com.example.healthtracker.room_data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [UserDao::class], version = 1)
abstract class UserDB :RoomDatabase(){
    abstract fun dao():UserDao
}