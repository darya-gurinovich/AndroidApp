package com.example.dashagurinovich.androidapp.services

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.dashagurinovich.androidapp.MainActivity
import com.example.dashagurinovich.androidapp.R
import com.example.dashagurinovich.androidapp.fragments.ProfileFragment
import com.example.dashagurinovich.androidapp.model.Profile
import com.example.dashagurinovich.androidapp.model.ProfileViewModel
import com.example.dashagurinovich.androidapp.model.ProfileViewModelFactory
import com.example.dashagurinovich.androidapp.storage.IStorage
import kotlinx.android.synthetic.main.fragment_profile.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class ProfileService(private val activity: Activity, private val storage : IStorage) {

    private var takenPhotoPath = ""
    private var profileViewModel: ProfileViewModel? = null
    
    init {
        val factory = ProfileViewModelFactory(storage)
        if (activity is FragmentActivity) {
            profileViewModel = ViewModelProviders.of(activity, factory)
                    .get(ProfileViewModel::class.java)
        }
    }

    fun getProfile() : Profile {
        return  profileViewModel?.getProfile() ?: Profile()
    }

    fun uploadPhoto() {
        val builder = AlertDialog.Builder(activity)

        builder.setPositiveButton("Take Photo") { _, _ ->
            getImageFromCamera()
        }
                .setNeutralButton("Upload from gallery") { _, _ ->
                    getImageFromGallery()
                }

        builder.show()
    }

    fun getImageFromGallery() {
        val permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.READ_EXTERNAL_STORAGE)

        //If the permission was denied show the dialog window to ask the permission
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    MainActivity.REQUEST_PERMISSION_EXTERNAL_STORAGE)
            return
        }
        else {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activity.startActivityForResult(gallery, MainActivity.REQUEST_OPEN_GALLERY)
        }
    }

    fun getImageFromCamera() {
        val permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.CAMERA)

        //If the permission was denied show the dialog window to ask the permission
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    arrayOf(Manifest.permission.CAMERA),
                    MainActivity.REQUEST_PERMISSION_CAMERA)
            return
        }
        else {
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->

                // Ensure that there's a camera activity to handle the intent
                takePictureIntent.resolveActivity(activity.packageManager)?.also { _ ->
                    // Create the File where the photo should go
                    val photoFile: File? = try {
                        getPhotoFile()
                    } catch (ex: IOException) {
                        null
                    }

                    // Continue only if the File was successfully created
                    photoFile?.also {
                        val photoURI: Uri = FileProvider.getUriForFile(
                                activity,
                                "com.example.android.fileprovider",
                                it
                        )

                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        activity.startActivityForResult(takePictureIntent,
                                MainActivity.REQUEST_OPEN_CAMERA)
                    }
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun getPhotoFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())
        val storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
                "JPEG_${timeStamp}_", /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            takenPhotoPath = absolutePath
        }
    }

    fun getProfileInfo() : Profile {
        return storage.getProfile() ?: Profile()
    }

    fun saveProfileInfo(profile: Profile) {
        storage.saveProfile(profile)
        profileViewModel?.updateProfile(profile)
    }

    fun getChangeMode(): Boolean {
        return profileViewModel?.isChangeMode ?: false
    }

    fun setChangeMode(isChangeMode : Boolean) {
        profileViewModel?.isChangeMode = isChangeMode
    }

    fun setObserver(profileFragment: ProfileFragment) { 
        val profileObserver = Observer<Profile> { newProfile ->

            if (newProfile != null) {
                profileFragment.nameTextView.text =
                        if (newProfile.name == "") activity.getString(R.string.no_info)
                        else newProfile.name

                profileFragment.nameEditView.text = Editable.Factory.getInstance()
                        .newEditable(newProfile.name)

                profileFragment.nameEditView.hint =
                        if (newProfile.name == "") activity.getString(R.string.no_info) else ""

                profileFragment.surnameTextView.text =
                        if (newProfile.surname == "") activity.getString(R.string.no_info)
                        else newProfile.surname

                profileFragment.surnameEditView.text = Editable.Factory.getInstance()
                        .newEditable(newProfile.surname)

                profileFragment.surnameEditView.hint =
                        if (newProfile.surname == "") activity.getString(R.string.no_info) else ""

                profileFragment.phoneTextView.text =
                        if (newProfile.phone == "") activity.getString(R.string.no_info)
                        else newProfile.phone

                profileFragment.phoneEditView.text = Editable.Factory.getInstance()
                        .newEditable(newProfile.phone)

                profileFragment.phoneEditView.hint =
                        if (newProfile.phone == "") activity.getString(R.string.no_info) else ""

                profileFragment.emailTextView.text =
                        if (newProfile.email == "") activity.getString(R.string.no_info)
                        else newProfile.email

                profileFragment.emailEditView.text = Editable.Factory.getInstance()
                        .newEditable(newProfile.email)

                profileFragment.emailEditView.hint =
                        if (newProfile.email == "") activity.getString(R.string.no_info) else ""

                profileFragment.profilePhoto.setImageBitmap(getBitmap(newProfile.imagePath))
            }
        }

        if (activity is LifecycleOwner)
            profileViewModel?.profile?.observe(activity, profileObserver)
    }

    private fun getBitmap(filePath: String): Bitmap {
        val file = File(filePath)

        if (!file.exists()) return BitmapFactory.decodeResource(activity.resources, 
                R.drawable.user_profile)

        return BitmapFactory.decodeFile(filePath)
    }

    fun updatePhoto(photoPath : Uri? = null) {
        val path = if (photoPath == null) takenPhotoPath
        else getPhotoPath(photoPath)
        profileViewModel?.updateProfilePhoto(path)
        storage.savePhoto(path)
    }

    private fun getPhotoPath(uri: Uri) : String {
        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)

        val cursor = activity.contentResolver.query(uri,
                filePathColumn, null, null, null)
        cursor!!.moveToFirst()

        val columnIndex = cursor.getColumnIndex(filePathColumn[0])
        val picturePath = cursor.getString(columnIndex)
        cursor.close()

        return picturePath
    }
}