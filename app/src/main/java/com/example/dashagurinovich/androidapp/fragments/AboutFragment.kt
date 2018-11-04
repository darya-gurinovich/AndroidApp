package com.example.dashagurinovich.androidapp.fragments


import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.TelephonyManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import com.example.dashagurinovich.androidapp.BuildConfig
import com.example.dashagurinovich.androidapp.MainActivity
import com.example.dashagurinovich.androidapp.R
import kotlinx.android.synthetic.main.fragment_about.*

class AboutFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_about, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addPhoneImeiToLayout()
        addCurrentAppVersionToLayout()
    }


    private fun addPhoneImeiToLayout(){
        if (activity !is MainActivity) return

        val mainActivity = activity as MainActivity

        val permission = ActivityCompat.checkSelfPermission(mainActivity,
                Manifest.permission.READ_PHONE_STATE)

        //If the permission was denied show the dialog window to ask the permission
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mainActivity,
                    arrayOf(Manifest.permission.READ_PHONE_STATE),
                    MainActivity.REQUEST_PERMISSION_PHONE_STATE)
            return
        }

        else {

            val imei = mainActivity.getImei()
            //Add the IMEI if the permission was granted

            phoneImeiTextView.text = imei
        }
    }

    private fun addCurrentAppVersionToLayout(){
        val appVersion = BuildConfig.VERSION_NAME
        versionTextView.text = appVersion
    }


}
