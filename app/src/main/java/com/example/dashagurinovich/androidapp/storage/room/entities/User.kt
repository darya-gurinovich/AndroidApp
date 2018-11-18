package com.example.dashagurinovich.androidapp.storage.room.entities

import androidx.room.*

@Entity(tableName = "users")
data class User(
        @ColumnInfo(name = "login") var login: String,
        @ColumnInfo(name = "password") var password: String,
        @ColumnInfo(name = "isCurrentUser") var isCurrentUser: Boolean
) {
    @PrimaryKey(autoGenerate = true) var id: Int = 0
}