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

    private val _requestPermissionPhoneState = 1

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        addPhoneImeiToLayout()
        addCurrentAppVersionToLayout()
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            _requestPermissionPhoneState -> {
                var imei : String
                //Show the IMEI if the permission was granted
                if ((grantResults.isNotEmpty() &&
                                grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

                    val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE)
                            as TelephonyManager
                    imei = telephonyManager.deviceId
                    if (imei == null) imei = "No information"
                } else {
                    imei = "No information"
                }

                val phoneImeiTextView = findViewById<TextView>(R.id.phoneImeiTextView)
                phoneImeiTextView.text = imei
                return
            }

            else -> {
                return
            }
        }
    }

    private fun addPhoneImeiToLayout(){
        var permission = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE)

        //If the permission was denied show the dialog window to ask the permission
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.READ_PHONE_STATE), _requestPermissionPhoneState)
            return
        }

        //Add the IMEI if the permission was granted
        val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        var imei = telephonyManager.deviceId
        if (imei == null) imei = "No information"

        val phoneImeiTextView = findViewById<TextView>(R.id.phoneImeiTextView)
        phoneImeiTextView.text = imei
    }

    private fun addCurrentAppVersionToLayout(){
        val appVersion = BuildConfig.VERSION_NAME
        val versionTextView = findViewById<TextView>(R.id.versionTextView)
        versionTextView.text = appVersion
    }
}
