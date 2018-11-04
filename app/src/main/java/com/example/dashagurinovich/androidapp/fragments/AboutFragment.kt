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
import com.example.dashagurinovich.androidapp.R
import kotlinx.android.synthetic.main.fragment_about.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class AboutFragment : Fragment() {

    private val _requestPermissionPhoneState = 1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_about, container, false)
    }

//    override fun onRequestPermissionsResult(requestCode: Int,
//                                            permissions: Array<String>, grantResults: IntArray) {
//        when (requestCode) {
//            _requestPermissionPhoneState -> {
//                var imei = getString(R.string.no_information)
//                //Show the IMEI if the permission was granted
//                if ((grantResults.isNotEmpty() &&
//                                grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
//                    imei = getImei()
//                }
//                //Show the explanation for the permission if it was denied
//                else if ((grantResults.isNotEmpty() &&
//                                grantResults[0] == PackageManager.PERMISSION_DENIED)) {
//                    showPermissionExplanation(Manifest.permission.READ_PHONE_STATE,
//                            getString(R.string.read_phone_state_permission_explanation),
//                            _requestPermissionPhoneState)
//                }
//                phoneImeiTextView.text = imei
//                return
//            }
//        }
//    }
//
//    private fun showPermissionExplanation (permission : String, explanation : String,
//                                           permissionRequestCode: Int) {
//        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
//            val builder = AlertDialog.Builder(this)
//            var dialogQuestion = getString(R.string.permission_explanation_dialog_question)
//            builder.setMessage("$explanation $dialogQuestion")
//                    .setTitle(R.string.permission_explanation_dialog_title)
//
//            builder.setPositiveButton("Yes"){ _, _ ->
//                // Do nothing if the user doesn't want to give the permission
//            }
//                    // Show the permission dialog again
//                    .setNegativeButton("No") { _, _ ->
//                        ActivityCompat.requestPermissions(this,
//                                arrayOf(permission), permissionRequestCode)
//
//                    }
//
//            builder.show()
//        }
//    }
//
//    private fun addPhoneImeiToLayout(){
//        var permission = ActivityCompat.checkSelfPermission(this,
//                Manifest.permission.READ_PHONE_STATE)
//
//        //If the permission was denied show the dialog window to ask the permission
//        if (permission != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,
//                    arrayOf(Manifest.permission.READ_PHONE_STATE), _requestPermissionPhoneState)
//            return
//        }
//
//        //Add the IMEI if the permission was granted
//        var imei = getImei()
//
//        phoneImeiTextView.text = imei
//    }
//
//    private fun getImei() : String{
//        val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
//        var imei = telephonyManager.deviceId
//        if (imei == null) imei = getString(R.string.no_information)
//        return imei
//    }
//
//    private fun addCurrentAppVersionToLayout(){
//        val appVersion = BuildConfig.VERSION_NAME
//        versionTextView.text = appVersion
//    }


}
