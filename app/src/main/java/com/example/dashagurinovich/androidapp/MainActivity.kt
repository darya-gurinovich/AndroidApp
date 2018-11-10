package com.example.dashagurinovich.androidapp

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.telephony.TelephonyManager
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import com.example.dashagurinovich.androidapp.storage.IStorage
import com.example.dashagurinovich.androidapp.model.Profile
import com.example.dashagurinovich.androidapp.storage.XMLStorage
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_about.phoneImeiTextView
import kotlinx.android.synthetic.main.fragment_profile.profilePhoto
import kotlinx.android.synthetic.main.nav_header.*
import java.io.File


class MainActivity : AppCompatActivity() {

    companion object {
        const val REQUEST_PERMISSION_PHONE_STATE = 1
        const val REQUEST_PERMISSION_EXTERNAL_STORAGE = 2
        const val REQUEST_OPEN_GALLERY = 3
        const val REQUEST_PERMISSION_CAMERA = 4
        const val REQUEST_OPEN_CAMERA = 5
        const val REQUEST_OPEN_PHOTO = 6
    }

    private var imei = ""
    private lateinit var storage : IStorage

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        imei = getString(R.string.no_info)

        setSupportActionBar(toolbar)

        val navController = Navigation.findNavController(this, R.id.main_activity_fragment)
        setupSideNavMenu(navController)
        setupActionBar(navController)

        storage = XMLStorage(File(applicationContext.filesDir, "data.xml"))
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
        val navController = Navigation.findNavController(this, R.id.main_activity_fragment)
        val navigated = NavigationUI.onNavDestinationSelected(item!!, navController)
        return navigated || super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        return Navigation.findNavController(this, R.id.main_activity_fragment)
                .navigateUp(drawer_layout)
    }

    fun getImei() : String {
        val permission = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE)

        //If the permission was denied show the dialog window to ask the permission
        if (permission == PackageManager.PERMISSION_GRANTED) {
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

                phoneImeiTextView.text = this.imei
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
            var dialogQuestion = getString(R.string.permission_explanation_dialog_question)
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

    private fun calculateImei() {
        val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE)
                as TelephonyManager
        var imei = telephonyManager.deviceId
        if (imei == null) this.imei = getString(R.string.no_info)
        this.imei = imei
    }

    fun getImageFromGallery() {
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(gallery, MainActivity.REQUEST_OPEN_GALLERY)
    }

    fun getImageFromCamera() {
        val camera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(camera, MainActivity.REQUEST_OPEN_CAMERA)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK || data == null) return
        when (requestCode) {
            MainActivity.REQUEST_OPEN_GALLERY -> {
                val selectedImage = data.data ?: return
                profilePhoto.setImageURI(selectedImage)

                storage.savePhoto(selectedImage)
            }
            MainActivity.REQUEST_OPEN_CAMERA -> {
                val photo = data.extras?.get("data") ?: return
                profilePhoto.setImageBitmap(photo as Bitmap)

                val uri = data.data ?: return
                storage.savePhoto(uri)
            }
            MainActivity.REQUEST_OPEN_PHOTO -> {
                val selectedImage = data.data ?: return
                profilePhoto.setImageURI(selectedImage)
            }
        }
    }

    fun getProfileInfo() : Profile {
        val profile = storage.getProfile() ?: Profile()
        return profile
    }

    fun saveProfileInfo(profile: Profile) {
        storage.saveProfile(profile)
    }

}
