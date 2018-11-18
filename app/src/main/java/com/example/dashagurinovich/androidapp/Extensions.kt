package com.example.dashagurinovich.androidapp

import android.app.Activity
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat

fun Activity.showPermissionExplanation (permission : String, explanation : String,
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