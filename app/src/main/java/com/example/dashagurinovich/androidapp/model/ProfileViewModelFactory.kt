package com.example.dashagurinovich.androidapp.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.dashagurinovich.androidapp.storage.IStorage

class ProfileViewModelFactory(private val storage : IStorage) :
        ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ProfileViewModel(storage) as T
    }
}