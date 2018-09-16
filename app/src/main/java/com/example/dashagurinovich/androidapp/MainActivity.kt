package com.example.dashagurinovich.androidapp

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.telephony.TelephonyManager
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        addPhoneImeiToLayout()
        addCurrentAppVersionToLayout()
    }

    private fun addPhoneImeiToLayout(){
        val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val permission = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE)

        var imei: String
        if (permission == PackageManager.PERMISSION_GRANTED) {
            imei = telephonyManager.deviceId
            if (imei == null) imei = "No information"
        }
        else {
            imei = "No information"
        }

        val phoneImeiTextView = findViewById<TextView>(R.id.phoneImeiTextView)
        phoneImeiTextView.text = imei
    }

    private fun addCurrentAppVersionToLayout(){
        val appVersion = BuildConfig.VERSION_NAME
        val versionTextView = findViewById<TextView>(R.id.versionTextView)
        versionTextView.text = appVersion
    }
}
