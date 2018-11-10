package com.example.dashagurinovich.androidapp.interfaces

import com.example.dashagurinovich.androidapp.fragments.ProfileFragment
import com.example.dashagurinovich.androidapp.model.Profile

interface IProfileManager {

    fun getProfileInfo() : Profile

    fun saveProfileInfo(profile: Profile)

    fun uploadPhoto()

    fun setObserver(profileFragment: ProfileFragment)

}