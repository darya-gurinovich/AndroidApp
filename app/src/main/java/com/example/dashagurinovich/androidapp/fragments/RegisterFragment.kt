package com.example.dashagurinovich.androidapp.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.dashagurinovich.androidapp.R
import com.example.dashagurinovich.androidapp.interfaces.IRegisterManager
import kotlinx.android.synthetic.main.fragment_register.*

class RegisterFragment : Fragment() {

    private var registerManager : IRegisterManager? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if (context is IRegisterManager)
            registerManager = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registerButton.setOnClickListener {
            val login = loginEditText.text.toString()
            val password = passwordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()

            if (password == confirmPassword)
                registerManager?.register(login, password)
        }
    }
}
