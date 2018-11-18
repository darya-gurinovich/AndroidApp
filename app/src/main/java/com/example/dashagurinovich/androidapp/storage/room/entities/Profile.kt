package com.example.dashagurinovich.androidapp.storage.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey

//@Entity(tableName = "profiles", foreignKeys = [ForeignKey(entity = User::class,
//        parentColumns = ["id"], childColumns = ["userId"], onDelete = CASCADE )])
@Entity(tableName = "profiles")
data class Profile(
        @ColumnInfo(name = "surname") var surname: String,
        @ColumnInfo(name = "name") var name: String,
        @ColumnInfo(name = "email") var email: String,
        @ColumnInfo(name = "phone") var phone: String,
        @ColumnInfo(name = "imagePath") var imagePath: String,
        @ColumnInfo(name = "userId") var userId: Int
){
    @PrimaryKey(autoGenerate = true) var id: Int = 0
}
