package com.example.dashagurinovich.androidapp.model

import android.net.Uri

class Profile {
    var surname : String = "No information"
    var name : String = "No information"
    var email: String ="No information"
    var phone : String = "No information"
    var imagePath : String = "android.resource://com.example.dashagurinovich" +
            ".androidapp/drawable/user_profile.jpg"

    constructor(surname : String, name : String, email : String, phone : String) {
        this.surname = surname
        this.name = name
        this.email = email
        this.phone = phone
    }

    constructor()

}