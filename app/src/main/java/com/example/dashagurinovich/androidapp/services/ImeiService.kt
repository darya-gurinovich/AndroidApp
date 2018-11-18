package com.example.dashagurinovich.androidapp.services

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.telephony.TelephonyManager
import androidx.core.app.ActivityCompat
import com.example.dashagurinovich.androidapp.MainActivity

@Suppress("DEPRECATION")
class ImeiService(private val activity: Activity) {
    private var imei = "No information"

    fun getImei() : String {

        val permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.READ_PHONE_STATE)

        //If the permission was denied show the dialog window to ask the permission
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    arrayOf(Manifest.permission.READ_PHONE_STATE),
                    MainActivity.REQUEST_PERMISSION_PHONE_STATE)
        }

        else {
            calculateImei()
        }

        return this.imei
    }

    @SuppressLint("HardwareIds", "MissingPermission")
    private fun calculateImei() {
        val telephonyManager = activity.getSystemService(Context.TELEPHONY_SERVICE)
                as TelephonyManager
        this.imei = telephonyManager.deviceId
    }
}