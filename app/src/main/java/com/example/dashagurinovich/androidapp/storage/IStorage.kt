package com.example.dashagurinovich.androidapp.storage

import com.example.dashagurinovich.androidapp.model.Profile
import com.example.dashagurinovich.androidapp.storage.room.entities.User

interface IStorage {
    fun createUser(user: User) : Boolean
    fun authenticateUser(login: String, password: String) : Boolean

    fun saveProfile(profile: Profile)
    fun getProfile() : Profile?

    fun savePhoto(photoPath: String)
}