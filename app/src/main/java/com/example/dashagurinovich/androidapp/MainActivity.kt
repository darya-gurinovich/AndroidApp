package com.example.dashagurinovich.androidapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.telephony.TelephonyManager
import android.text.Editable
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import com.example.dashagurinovich.androidapp.fragments.ProfileFragment
import com.example.dashagurinovich.androidapp.interfaces.IAnimationHandler
import com.example.dashagurinovich.androidapp.interfaces.IImeiManager
import com.example.dashagurinovich.androidapp.interfaces.IProfileManager
import com.example.dashagurinovich.androidapp.model.Profile
import com.example.dashagurinovich.androidapp.model.ProfileViewModel
import com.example.dashagurinovich.androidapp.model.ProfileViewModelFactory
import com.example.dashagurinovich.androidapp.storage.IStorage
import com.example.dashagurinovich.androidapp.storage.XMLStorage
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.nav_header.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity(), IAnimationHandler, IImeiManager, IProfileManager {

    companion object {
        const val REQUEST_PERMISSION_PHONE_STATE = 1
        const val REQUEST_PERMISSION_EXTERNAL_STORAGE = 2
        const val REQUEST_OPEN_GALLERY = 3
        const val REQUEST_PERMISSION_CAMERA = 4
        const val REQUEST_OPEN_CAMERA = 5
    }

    private var imei = ""
    private lateinit var storage : IStorage
    private lateinit var profileViewModel: ProfileViewModel

    private var takenPhotoPath : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        imei = getString(R.string.no_info)

        storage = XMLStorage(File(applicationContext.filesDir, "data.xml"))

        val factory = ProfileViewModelFactory(storage)
        profileViewModel = ViewModelProviders.of(this, factory)
                .get(ProfileViewModel::class.java)

        setSupportActionBar(toolbar)

        val navController = Navigation.findNavController(this, R.id.main_activity_fragment)
        setupSideNavMenu(navController)
        setupActionBar(navController)
    }

    private fun setupSideNavMenu(navController: NavController) {
        navigation_view?.let {
            NavigationUI.setupWithNavController(it, navController)
        }
    }

    private fun setupActionBar(navController: NavController) {
        NavigationUI.setupActionBarWithNavController(this, navController, drawer_layout)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val profile = profileViewModel.getProfile()
        profileNavName.text = String.format(getString(R.string.person_full_name),
                profile.name, profile.surname)
        profileNavPhoto.setImageBitmap(getBitmap(profile.imagePath))

        val navController = Navigation.findNavController(this, R.id.main_activity_fragment)
        val navigated = NavigationUI.onNavDestinationSelected(item!!, navController)
        return navigated || super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        return Navigation.findNavController(this, R.id.main_activity_fragment)
                .navigateUp(drawer_layout)
    }

    override fun setObserver(profileFragment: ProfileFragment) {

        if (!::profileViewModel.isInitialized) profileViewModel = ProfileViewModel(storage)

        val profileObserver = Observer<Profile> { newProfile ->

            if (newProfile != null) {
                profileFragment.nameTextView.text = newProfile.name
                profileFragment.nameEditView.text = Editable.Factory.getInstance()
                        .newEditable(newProfile.name)

                profileFragment.surnameTextView.text = newProfile.surname
                profileFragment.surnameEditView.text = Editable.Factory.getInstance()
                        .newEditable(newProfile.surname)

                profileFragment.phoneTextView.text = newProfile.phone
                profileFragment.phoneEditView.text = Editable.Factory.getInstance()
                        .newEditable(newProfile.phone)

                profileFragment.emailTextView.text = newProfile.email
                profileFragment.emailEditView.text = Editable.Factory.getInstance()
                        .newEditable(newProfile.email)

                profileFragment.profilePhoto.setImageBitmap(getBitmap(newProfile.imagePath))
            }
        }

        profileViewModel.profile.observe(this, profileObserver)
    }

    private fun getBitmap(filePath: String): Bitmap {
        val file = File(filePath)

        if (!file.exists()) return BitmapFactory.decodeResource(resources, R.drawable.user_profile)

        return BitmapFactory.decodeFile(filePath)
    }

    override fun getImei() : String? {

        val permission = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE)

        //If the permission was denied show the dialog window to ask the permission
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.READ_PHONE_STATE),
                    MainActivity.REQUEST_PERMISSION_PHONE_STATE)
        }

        else {
            calculateImei()
        }

        return this.imei
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MainActivity.REQUEST_PERMISSION_PHONE_STATE -> {
                //Show the IMEI if the permission was granted
                if ((grantResults.isNotEmpty() &&
                                grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    calculateImei()
                }

                //Show the explanation for the permission if it was denied
                else if ((grantResults.isNotEmpty() &&
                                grantResults[0] == PackageManager.PERMISSION_DENIED)) {
                    showPermissionExplanation(Manifest.permission.READ_PHONE_STATE,
                            getString(R.string.read_phone_state_permission_explanation),
                            MainActivity.REQUEST_PERMISSION_PHONE_STATE)
                }
                return
            }
            MainActivity.REQUEST_PERMISSION_EXTERNAL_STORAGE -> {
                //Open the gallery
                if ((grantResults.isNotEmpty() &&
                                grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    getImageFromGallery()
                }
                //Show the explanation for the permission if it was denied
                else if ((grantResults.isNotEmpty() &&
                                grantResults[0] == PackageManager.PERMISSION_DENIED)) {
                    showPermissionExplanation(Manifest.permission.READ_EXTERNAL_STORAGE,
                            getString(R.string.read_external_storage_permission_explanation),
                            MainActivity.REQUEST_PERMISSION_EXTERNAL_STORAGE)
                }
            }
            MainActivity.REQUEST_PERMISSION_CAMERA -> {
                //Open the gallery
                if ((grantResults.isNotEmpty() &&
                                grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    getImageFromCamera()
                }
                //Show the explanation for the permission if it was denied
                else if ((grantResults.isNotEmpty() &&
                                grantResults[0] == PackageManager.PERMISSION_DENIED)) {
                    showPermissionExplanation(Manifest.permission.CAMERA,
                            getString(R.string.camera_permission_explanation),
                            MainActivity.REQUEST_PERMISSION_CAMERA)
                }
            }
        }
    }

    private fun showPermissionExplanation (permission : String, explanation : String,
                                           permissionRequestCode: Int) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        permission)) {
            val builder = AlertDialog.Builder(this)
            val dialogQuestion = getString(R.string.permission_explanation_dialog_question)
            builder.setMessage("$explanation $dialogQuestion")
                    .setTitle(R.string.permission_explanation_dialog_title)

            builder.setPositiveButton("Yes"){ _, _ ->
                // Do nothing if the user doesn't want to give the permission
            }
                    // Show the permission dialog again
                    .setNegativeButton("No") { _, _ ->
                        ActivityCompat.requestPermissions(this,
                                arrayOf(permission), permissionRequestCode)

                    }

            builder.show()
        }
    }

    @SuppressLint("HardwareIds")
    private fun calculateImei() {
        val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE)
                as TelephonyManager
        var imei = telephonyManager.deviceId
        if (imei == null) this.imei = getString(R.string.no_info)
        this.imei = imei
    }

    override fun uploadPhoto() {
        val builder = AlertDialog.Builder(this)

        builder.setPositiveButton(getString(R.string.take_photo)) { _, _ ->
            getImageFromCamera()
        }
                .setNeutralButton(getString(R.string.upload_from_gallery)) { _, _ ->
                    getImageFromGallery()
                }

        builder.show()
    }

    private fun getImageFromGallery() {
        val permission = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)

        //If the permission was denied show the dialog window to ask the permission
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    MainActivity.REQUEST_PERMISSION_EXTERNAL_STORAGE)
            return
        }
        else {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(gallery, MainActivity.REQUEST_OPEN_GALLERY)
        }
    }

    private fun getImageFromCamera() {
        val permission = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)

        //If the permission was denied show the dialog window to ask the permission
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.CAMERA),
                    MainActivity.REQUEST_PERMISSION_CAMERA)
            return
        }
        else {
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->

                // Ensure that there's a camera activity to handle the intent
                takePictureIntent.resolveActivity(packageManager)?.also { _ ->
                    // Create the File where the photo should go
                    val photoFile: File? = try {
                        getPhotoFile()
                    } catch (ex: IOException) {
                        null
                    }

                    // Continue only if the File was successfully created
                    photoFile?.also {
                        val photoURI: Uri = FileProvider.getUriForFile(
                                this,
                                "com.example.android.fileprovider",
                                it
                        )

                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        startActivityForResult(takePictureIntent, REQUEST_OPEN_CAMERA)
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK || data == null) return
        when (requestCode) {
            MainActivity.REQUEST_OPEN_GALLERY -> {
                val selectedImage = data.data ?: return

                val picturePath = getPhotoPath(selectedImage)

                profileViewModel.updateProfilePhoto(picturePath)
                storage.savePhoto(picturePath)
            }
            MainActivity.REQUEST_OPEN_CAMERA -> {
                takenPhotoPath?.let {
                    profileViewModel.updateProfilePhoto(takenPhotoPath!!)
                    storage.savePhoto(takenPhotoPath!!)
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun getPhotoFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
                "JPEG_${timeStamp}_", /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            takenPhotoPath = absolutePath
        }
    }

    private fun getPhotoPath(uri: Uri) : String {
        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)

        val cursor = contentResolver.query(uri,
                filePathColumn, null, null, null)
        cursor!!.moveToFirst()

        val columnIndex = cursor.getColumnIndex(filePathColumn[0])
        val picturePath = cursor.getString(columnIndex)
        cursor.close()

        return picturePath
    }

    override fun getProfileInfo() : Profile {
        return storage.getProfile() ?: Profile()
    }

    override fun saveProfileInfo(profile: Profile) {
        storage.saveProfile(profile)
        profileViewModel.updateProfile(profile)
    }

    override fun animateRotateForward(view : View) {
        val rotateForwardAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_forward)
        view.startAnimation(rotateForwardAnim)
    }

    override fun animateRotateBackward(view : View) {
        val rotateBackwardAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_backward)
        view.startAnimation(rotateBackwardAnim)
    }

    override fun getChangeMode(): Boolean {
        return profileViewModel.isChangeMode
    }

    override fun setChangeMode(isChangeMode : Boolean) {
        profileViewModel.isChangeMode = isChangeMode
    }
}
