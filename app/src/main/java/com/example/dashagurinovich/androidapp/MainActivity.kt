package com.example.dashagurinovich.androidapp

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import com.example.dashagurinovich.androidapp.fragments.ProfileFragment
import com.example.dashagurinovich.androidapp.interfaces.IAnimationHandler
import com.example.dashagurinovich.androidapp.interfaces.IImeiManager
import com.example.dashagurinovich.androidapp.interfaces.IProfileManager
import com.example.dashagurinovich.androidapp.model.Profile
import com.example.dashagurinovich.androidapp.services.AnimationService
import com.example.dashagurinovich.androidapp.services.ImeiService
import com.example.dashagurinovich.androidapp.services.ProfileService
import com.example.dashagurinovich.androidapp.storage.XMLStorage
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header.*
import java.io.File


class MainActivity : AppCompatActivity(), IAnimationHandler, IImeiManager, IProfileManager {

    companion object {
        const val REQUEST_PERMISSION_PHONE_STATE = 1
        const val REQUEST_PERMISSION_EXTERNAL_STORAGE = 2
        const val REQUEST_OPEN_GALLERY = 3
        const val REQUEST_PERMISSION_CAMERA = 4
        const val REQUEST_OPEN_CAMERA = 5
    }

    private val animationService = AnimationService(this)
    private val imeiService = ImeiService(this)
    private lateinit var profileService : ProfileService

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val storage = XMLStorage(File(applicationContext.filesDir, "data.xml"))
        profileService = ProfileService(this, storage)

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
        val profile = profileService.getProfile()

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

    private fun getBitmap(filePath: String): Bitmap {
        val file = File(filePath)

        if (!file.exists()) return BitmapFactory.decodeResource(resources, R.drawable.user_profile)

        return BitmapFactory.decodeFile(filePath)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MainActivity.REQUEST_PERMISSION_PHONE_STATE -> {
                //Show the IMEI if the permission was granted
                if ((grantResults.isNotEmpty() &&
                                grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    imeiService.getImei()
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
                    profileService.getImageFromGallery()
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
                    profileService.getImageFromCamera()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK || data == null) return
        when (requestCode) {
            MainActivity.REQUEST_OPEN_GALLERY -> {
                val selectedImage = data.data ?: return

                profileService.updatePhoto(selectedImage)
            }
            MainActivity.REQUEST_OPEN_CAMERA -> {
                profileService.updatePhoto()
            }
        }
    }

    override fun getImei() : String {
        return imeiService.getImei()
    }

    override fun setObserver(profileFragment: ProfileFragment) {
        profileService.setObserver(profileFragment)
    }

    override fun uploadPhoto() {
        profileService.uploadPhoto()
    }

    override fun getProfileInfo() : Profile {
        return profileService.getProfileInfo()
    }

    override fun saveProfileInfo(profile: Profile) {
        profileService.saveProfileInfo(profile)
    }

    override fun animateRotateForward(view : View) {
        animationService.animateRotateForward(view)
    }

    override fun animateRotateBackward(view : View) {
        animationService.animateRotateBackward(view)
    }

    override fun getChangeMode(): Boolean {
        return profileService.getChangeMode()
    }

    override fun setChangeMode(isChangeMode : Boolean) {
        profileService.setChangeMode(isChangeMode)
    }
}
