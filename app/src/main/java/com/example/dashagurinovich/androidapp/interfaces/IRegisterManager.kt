package com.example.dashagurinovich.androidapp.interfaces

interface IRegisterManager {
    fun register(login : String, password: String) : Boolean
}