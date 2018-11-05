package com.example.dashagurinovich.androidapp.model

class Profile {
    var surname : String = "No information"
    var name : String = "No information"
    var email: String ="No information"
    var phone : String = "No information"

    constructor(surname : String, name : String, email : String, phone : String) {
        this.surname = surname
        this.name = name
        this.email = email
        this.phone = phone
    }

    constructor()

}