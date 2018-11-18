package com.example.dashagurinovich.androidapp.storage.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.example.dashagurinovich.androidapp.storage.room.entities.User

@Dao
interface UserDao {

    @Insert(onConflict = REPLACE)
    fun saveUser(user: User)

    @Query("SELECT * FROM users u WHERE u.isCurrentUser ")
    fun getCurrentUser() : User?

    @Query("SELECT * FROM users u WHERE u.login = :login AND u.password = :password ")
    fun getAuthenticatedUser(login: String, password: String) : User?

    @Query("SELECT * FROM users u")
    fun getUsers() : List<User>

    @Query("SELECT * FROM users u WHERE u.login = :login")
    fun getAuthenticatedUserByLogin(login: String) : User?

}