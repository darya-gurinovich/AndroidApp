package com.example.dashagurinovich.androidapp.fragments


import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.dashagurinovich.androidapp.R
import com.example.dashagurinovich.androidapp.interfaces.IAnimationHandler
import com.example.dashagurinovich.androidapp.interfaces.IProfileManager
import com.example.dashagurinovich.androidapp.model.Profile
import kotlinx.android.synthetic.main.fragment_profile.*
import android.opengl.ETC1.getHeight
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager


class ProfileFragment : Fragment() {

    private var isChangeMode : Boolean = false

    private lateinit var editViews : List<EditText>
    private lateinit var textViews : List<TextView>

    private var profileManager : IProfileManager? = null
    private var animationHandler : IAnimationHandler? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if (context is IProfileManager) {
            profileManager = context
        }

        if (context is IAnimationHandler)
            animationHandler = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onPause() {
        super.onPause()

        if (isChangeMode) {
            val profile = Profile(surnameEditView.text.toString(), nameEditView.text.toString(),
                    emailEditView.text.toString(), phoneEditView.text.toString())
            profileManager?.saveProfileInfo(profile)
        }

        profileManager?.setChangeMode(isChangeMode)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileManager?.setObserver(this)

        isChangeMode = profileManager?.getChangeMode() ?: false

        editViews = listOf<EditText>(surnameEditView, nameEditView, phoneEditView,
                emailEditView)
        textViews = listOf<TextView>(surnameTextView, nameTextView, phoneTextView,
                emailTextView)

        if (isChangeMode) changeProfile()

        profilePhoto.setOnClickListener { _ ->
            if (isChangeMode) {
                profileManager?.uploadPhoto()
            }
        }

        addKeyboardListener()

        changeProfileButton.setOnClickListener {
            isChangeMode = if (!isChangeMode) {
                animationHandler?.animateRotateForward(it)
                changeProfile()
                true
            } else {
                animationHandler?.animateRotateBackward(it)
                saveProfile()
                false
            }
        }

    }

    private fun addKeyboardListener() {
        profileView.viewTreeObserver.addOnGlobalLayoutListener(object: ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val r = Rect()
                profileView.getWindowVisibleDisplayFrame(r)
                val screenHeight = profileView.rootView.height

                val keypadHeight = screenHeight - r.bottom

                if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                    changeProfileButton.hide()
                } else {
                    changeProfileButton.show()
                }
            }
        })
    }

    private fun changeProfile() {
        for (editView in editViews)
            editView.visibility = View.VISIBLE

        for (textView in textViews)
            textView.visibility = View.GONE

        profilePhotoEditImage.visibility = View.VISIBLE
        changeProfileButton.setImageResource(R.drawable.ic_done)
    }

    private fun saveProfile() {
        for (editView in editViews)
            editView.visibility = View.GONE

        for (textView in textViews)
            textView.visibility = View.VISIBLE

        profilePhotoEditImage.visibility = View.INVISIBLE
        changeProfileButton.setImageResource(R.drawable.ic_edit)

        val profile = Profile(surnameEditView.text.toString(), nameEditView.text.toString(),
                emailEditView.text.toString(), phoneEditView.text.toString())
        profileManager?.saveProfileInfo(profile)
    }

}
