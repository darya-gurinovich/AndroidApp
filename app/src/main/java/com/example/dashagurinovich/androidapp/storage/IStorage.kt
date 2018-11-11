package com.example.dashagurinovich.androidapp.storage

import com.example.dashagurinovich.androidapp.model.Profile

interface IStorage {

    fun saveProfile(profile: Profile)
    fun getProfile() : Profile?

    fun savePhoto(photoPath: String)
}