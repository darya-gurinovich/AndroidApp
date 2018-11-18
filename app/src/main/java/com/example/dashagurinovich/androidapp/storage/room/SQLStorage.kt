package com.example.dashagurinovich.androidapp.storage.room

import com.example.dashagurinovich.androidapp.model.Profile
import com.example.dashagurinovich.androidapp.storage.IStorage
import com.example.dashagurinovich.androidapp.storage.room.entities.User
import org.mindrot.jbcrypt.BCrypt

class SQLStorage(private val appDataBase: AppDataBase) : IStorage {
    override fun signOutUser() {
        val user = appDataBase.userDao().getCurrentUser() ?: return

        user.isCurrentUser = false
        appDataBase.userDao().saveUser(user)
    }

    override fun createUser(user: User) : Boolean {
        if (appDataBase.userDao().getAuthenticatedUserByLogin(user.login) != null) return false

        user.password = BCrypt.hashpw(user.password, BCrypt.gensalt())
        appDataBase.userDao().saveUser(user)

        val newUser = appDataBase.userDao().getAuthenticatedUserByLogin(user.login) ?: return false
        val profile = Profile()
        val profileEntity = com.example.dashagurinovich.androidapp.storage.room.entities
                .Profile(profile.surname, profile.name, profile.email, profile.phone,
                        profile.imagePath, newUser.id)
        appDataBase.profileDao().saveProfile(profileEntity)
        return true
    }

    override fun authenticateUser(login: String, password: String) : Boolean {
        val user = appDataBase.userDao().getAuthenticatedUserByLogin(login) ?: return false

        if (BCrypt.hashpw(password, user.password) != user.password) return false

        user.isCurrentUser = true
        appDataBase.userDao().saveUser(user)

        return true
    }

    override fun saveProfile(profile: Profile) {
        val oldProfile = appDataBase.profileDao().getProfile() ?: return

        oldProfile.name = profile.name
        oldProfile.surname = profile.surname
        oldProfile.phone = profile.phone
        oldProfile.email = profile.email

        appDataBase.profileDao().saveProfile(oldProfile)
    }

    override fun getProfile(): Profile? {
        val profileEntity = appDataBase.profileDao().getProfile() ?: return null
        val profile = Profile(profileEntity.surname, profileEntity.name, profileEntity.email,
                profileEntity.phone)
        profile.imagePath = profileEntity.imagePath
        return profile
    }

    override fun savePhoto(photoPath: String) {
        val profileEntity = appDataBase.profileDao().getProfile() ?: return
        profileEntity.imagePath = photoPath
        appDataBase.profileDao().saveProfile(profileEntity)
    }
}