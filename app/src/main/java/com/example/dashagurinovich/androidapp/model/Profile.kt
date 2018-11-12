package com.example.dashagurinovich.androidapp.model

data class Profile(var surname : String, var name : String, var email : String, var phone : String) {

    var imagePath : String = "android.resource://com.example.dashagurinovich" +
            ".androidapp/drawable/user_profile.jpg"

    constructor() : this("", "", "",
            "")

}