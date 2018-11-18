package com.example.dashagurinovich.androidapp.services

import android.app.Activity
import android.view.View
import android.view.animation.AnimationUtils
import com.example.dashagurinovich.androidapp.R

class AnimationService(private val activity: Activity) {

    fun animateRotateForward(view : View) {
        val rotateForwardAnim = AnimationUtils.loadAnimation(activity, R.anim.rotate_forward)
        view.startAnimation(rotateForwardAnim)
    }

    fun animateRotateBackward(view : View) {
        val rotateBackwardAnim = AnimationUtils.loadAnimation(activity, R.anim.rotate_backward)
        view.startAnimation(rotateBackwardAnim)
    }
}