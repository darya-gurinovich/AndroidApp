package com.example.dashagurinovich.androidapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.dashagurinovich.androidapp.MainActivity
import com.example.dashagurinovich.androidapp.R
import com.example.dashagurinovich.androidapp.model.Profile
import kotlinx.android.synthetic.main.fragment_change_profile.*

class ChangeProfileFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_change_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            val safeArgs = ChangeProfileFragmentArgs.fromBundle(it)
            surnameEditText.setText(safeArgs.surname)
            nameEditText.setText(safeArgs.name)
            emailEditText.setText(safeArgs.email)
            phoneEditText.setText(safeArgs.phone)
        }

        saveProfileButton.setOnClickListener{
            saveProfile()
            Navigation.findNavController(it).navigate(R.id.destination_profile)
        }
    }

    private fun saveProfile() {
        if (activity !is MainActivity) return
        val mainActivity = activity as MainActivity

        val profile = Profile(surnameEditText.text.toString(), nameEditText.text.toString(),
                emailEditText.text.toString(), phoneEditText.text.toString())

        mainActivity.saveProfileInfo(profile)
    }
}