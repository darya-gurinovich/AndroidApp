package com.example.dashagurinovich.androidapp.storage

import android.net.Uri
import com.example.dashagurinovich.androidapp.model.Profile

interface IStorage {

    fun saveProfile(profile: Profile)
    fun getProfile() : Profile?

    fun savePhoto(photoUri: Uri)
}