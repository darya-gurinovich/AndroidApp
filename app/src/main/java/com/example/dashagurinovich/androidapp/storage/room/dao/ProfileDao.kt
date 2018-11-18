package com.example.dashagurinovich.androidapp.storage.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.example.dashagurinovich.androidapp.storage.room.entities.Profile

@Dao
interface ProfileDao {

    @Insert(onConflict = REPLACE)
    fun saveProfile(profile: Profile)

    @Query("SELECT * FROM users u, profiles p WHERE u.id = p.userId AND u.isCurrentUser")
    fun getProfile() : Profile?

    @Query("SELECT * FROM profiles p")
    fun getProfiles() : List<Profile>
}