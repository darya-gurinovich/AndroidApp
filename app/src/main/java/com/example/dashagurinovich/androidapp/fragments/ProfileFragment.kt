package com.example.dashagurinovich.androidapp.fragments


import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.example.dashagurinovich.androidapp.MainActivity
import com.example.dashagurinovich.androidapp.R
import kotlinx.android.synthetic.main.fragment_profile.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class ProfileFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (activity !is MainActivity) return
        val mainActivity = activity as MainActivity

        profilePhoto.setOnClickListener {
            val builder = AlertDialog.Builder(mainActivity)

            builder.setPositiveButton(getString(R.string.take_photo)){ _, _ ->
                // Do nothing if the user doesn't want to give the permission
            }
                    .setNeutralButton(getString(R.string.upload_from_gallery)) { _, _ ->
                        uploadFromGallery(mainActivity)
                    }

            builder.show()
        }
    }

    private fun uploadFromGallery(mainActivity: MainActivity) {
        val permission = ActivityCompat.checkSelfPermission(mainActivity,
                Manifest.permission.READ_EXTERNAL_STORAGE)

        //If the permission was denied show the dialog window to ask the permission
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mainActivity,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    MainActivity.REQUEST_PERMISSION_EXTERNAL_STORAGE)
            return
        }
        else {
            mainActivity.getImageFromGallery()
        }
    }


}
