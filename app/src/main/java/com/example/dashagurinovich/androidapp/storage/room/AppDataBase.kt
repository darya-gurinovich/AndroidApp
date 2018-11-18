package com.example.dashagurinovich.androidapp.storage.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.dashagurinovich.androidapp.storage.room.dao.ProfileDao
import com.example.dashagurinovich.androidapp.storage.room.dao.UserDao
import com.example.dashagurinovich.androidapp.storage.room.entities.Profile
import com.example.dashagurinovich.androidapp.storage.room.entities.User

@Database(entities = [User::class, Profile::class], version = 4)
abstract class AppDataBase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun profileDao(): ProfileDao

    companion object {
        private var instance: AppDataBase? = null
        fun getDatabase(context: Context): AppDataBase {
            if (instance == null) {
                instance = Room.databaseBuilder(
                        context,
                        AppDataBase::class.java,
                        "androidAppDatabase"
                ).fallbackToDestructiveMigration().allowMainThreadQueries().build()
            }
            return instance as AppDataBase
        }

        fun destroyDatabase() {
            instance = null
        }
    }
}