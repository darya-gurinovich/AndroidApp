package com.example.dashagurinovich.androidapp.fragments


import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.example.dashagurinovich.androidapp.MainActivity
import com.example.dashagurinovich.androidapp.R
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment() {

    private var isChangeMode : Boolean = false
    private lateinit var editViews : List<EditText>
    private lateinit var textViews : List<TextView>
    private lateinit var rotateForwardAnim : Animation
    private lateinit var rotateBackwardAnim : Animation

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editViews = listOf<EditText>(surnameEditView, nameEditView, phoneEditView,
                emailEditView)
        textViews = listOf<TextView>(surnameTextView, nameTextView, phoneTextView,
                emailTextView)

        if (activity !is MainActivity) return
        val mainActivity = activity as MainActivity

        rotateForwardAnim = AnimationUtils.loadAnimation(mainActivity, R.anim.rotate_forward)
        rotateBackwardAnim = AnimationUtils.loadAnimation(mainActivity, R.anim.rotate_backward)
        updateProfileInfo(mainActivity)

        profilePhoto.setOnClickListener {
            if (isChangeMode) {
                val builder = AlertDialog.Builder(mainActivity)

                builder.setPositiveButton(getString(R.string.take_photo)) { _, _ ->
                    uploadFromCamera(mainActivity)
                }
                        .setNeutralButton(getString(R.string.upload_from_gallery)) { _, _ ->
                            uploadFromGallery(mainActivity)
                        }

                builder.show()
            }
        }

        changeProfileButton.setOnClickListener {
            isChangeMode = if (!isChangeMode) {
                changeProfileButton.startAnimation(rotateForwardAnim)
                changeProfile()
                true
            } else {
                changeProfileButton.startAnimation(rotateBackwardAnim)
                saveProfile()
                false
            }
        }

    }

    private fun changeProfile() {
        for (editView in editViews)
            editView.visibility = View.VISIBLE

        for (textView in textViews)
            textView.visibility = View.GONE

        profilePhotoEditImage.visibility = View.VISIBLE
        changeProfileButton.setImageResource(R.drawable.ic_done)
    }

    private fun saveProfile() {
        for (editView in editViews)
            editView.visibility = View.GONE

        for (textView in textViews)
            textView.visibility = View.VISIBLE

        profilePhotoEditImage.visibility = View.INVISIBLE
        changeProfileButton.setImageResource(R.drawable.ic_edit)
    }

    private fun updateProfileInfo(mainActivity: MainActivity) {

        val profile = mainActivity.getProfileInfo()

        surnameTextView?.text = profile.surname
        nameTextView?.text = profile.name
        phoneTextView?.text = profile.phone
        emailTextView?.text = profile.email

        //profilePhoto.setImageURI(profile.imageUri)
    }

    private fun uploadFromGallery(mainActivity: MainActivity) {
        val permission = ActivityCompat.checkSelfPermission(mainActivity,
                Manifest.permission.READ_EXTERNAL_STORAGE)

        //If the permission was denied show the dialog window to ask the permission
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mainActivity,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    MainActivity.REQUEST_PERMISSION_EXTERNAL_STORAGE)
            return
        }
        else {
            mainActivity.getImageFromGallery()
        }
    }

    private fun uploadFromCamera(mainActivity: MainActivity) {
        val permission = ActivityCompat.checkSelfPermission(mainActivity,
                Manifest.permission.CAMERA)

        //If the permission was denied show the dialog window to ask the permission
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mainActivity,
                    arrayOf(Manifest.permission.CAMERA),
                    MainActivity.REQUEST_PERMISSION_CAMERA)
            return
        }
        else {
            mainActivity.getImageFromCamera()
        }
    }

}
