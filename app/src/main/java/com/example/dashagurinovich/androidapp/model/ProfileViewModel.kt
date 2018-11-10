package com.example.dashagurinovich.androidapp.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dashagurinovich.androidapp.storage.IStorage

class ProfileViewModel(storage : IStorage) : ViewModel() {
    var profile: MutableLiveData<Profile> = MutableLiveData()

    init {
        profile.value = storage.getProfile()
    }

    fun getProfile() = profile.value ?: Profile()

    fun updateProfile(newProfile: Profile) {
        profile.value = newProfile
    }
}