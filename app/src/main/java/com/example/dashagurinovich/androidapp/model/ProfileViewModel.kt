package com.example.dashagurinovich.androidapp.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dashagurinovich.androidapp.storage.IStorage

class ProfileViewModel(storage : IStorage) : ViewModel() {
    private var imagePath : String? = null
    var profile: MutableLiveData<Profile> = MutableLiveData()

    init {
        profile.value = storage.getProfile()
    }

    fun getProfile() = profile.value ?: Profile()

    fun updateProfile(newProfile: Profile) {
        if (imagePath != null) newProfile.imagePath = imagePath as String
        profile.value = newProfile
    }

    fun updateProfilePhoto(newProfilePhotoPath: String) {
        imagePath = newProfilePhotoPath

        val oldProfile = profile.value
        oldProfile?.imagePath = newProfilePhotoPath
        profile.value = oldProfile
    }
}