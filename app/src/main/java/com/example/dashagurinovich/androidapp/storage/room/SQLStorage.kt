package com.example.dashagurinovich.androidapp.storage.room

import com.example.dashagurinovich.androidapp.model.Profile
import com.example.dashagurinovich.androidapp.storage.IStorage
import com.example.dashagurinovich.androidapp.storage.room.entities.User

class SQLStorage(private val appDataBase: AppDataBase) : IStorage {
    override fun createUser(user: User) : Boolean {
        if (appDataBase.userDao().getAuthenticatedUserByLogin(user.login) != null) return false

        appDataBase.userDao().saveUser(user)

        val users = appDataBase.userDao().getUsers()
        val profile = Profile()
        val profileEntity = com.example.dashagurinovich.androidapp.storage.room.entities
                .Profile(profile.surname, profile.name, profile.email, profile.phone,
                        profile.imagePath, user.id)
        appDataBase.profileDao().saveProfile(profileEntity)
        return true
    }

    override fun authenticateUser(login: String, password: String) : Boolean {
        val user = appDataBase.userDao().getAuthenticatedUser(login, password) ?: return false
        user.isCurrentUser = true
        appDataBase.userDao().saveUser(user)
        return true
    }

    override fun saveProfile(profile: Profile) {
        val user = appDataBase.userDao().getCurrentUser() ?: return
        val profileEntity = com.example.dashagurinovich.androidapp.storage.room.entities
                .Profile(profile.surname, profile.name, profile.email, profile.phone,
                         profile.imagePath, user.id)
        appDataBase.profileDao().saveProfile(profileEntity)
    }

    override fun getProfile(): Profile? {
        val profileEntity = appDataBase.profileDao().getProfile() ?: return null
        return Profile(profileEntity.surname, profileEntity.name, profileEntity.email,
                profileEntity.phone)
    }

    override fun savePhoto(photoPath: String) {
        val profileEntity = appDataBase.profileDao().getProfile() ?: return
        profileEntity.imagePath = photoPath
        appDataBase.profileDao().saveProfile(profileEntity)
    }
}