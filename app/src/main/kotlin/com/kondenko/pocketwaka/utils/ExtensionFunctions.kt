package com.kondenko.pocketwaka.utils

import android.os.Build
import android.support.v4.view.ViewCompat
import android.view.View

fun View.elevation(elevation: Float) {
    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) ViewCompat.setElevation(this, elevation)
    else this.elevation = elevation
}