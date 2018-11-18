package com.example.dashagurinovich.androidapp.storage.room.entities

import androidx.room.*

@Entity
data class User(
        @PrimaryKey var uid: Int,
        @ColumnInfo(name = "login") var firstName: String?,
        @ColumnInfo(name = "password") var lastName: String?
)