package com.kondenko.pocketwaka.utils.extensions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.TypedValue
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import java.util.*

inline fun <reified T : Activity> Context.startActivity() {
    startActivity(Intent(this, T::class.java))
}

fun Context.getCurrentLocale(): Locale = resources.configuration.run {
    @Suppress("DEPRECATION")
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) locales[0]
    else locale
}

/**
 * Update a view's dimension so it matches the device's density
 */
fun Context.adjustForDensity(value: Float) = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, resources.displayMetrics)

/**
 * Update a view's dimension so it matches the device's density
 */
fun Context.adjustForDensity(value: Int) = adjustForDensity(value.toFloat())

fun Context.adjustForDensity(value: Int?): Float? {
    if (value == null) return null
    return adjustForDensity(value.toFloat())
}

fun Context.getColorCompat(@ColorRes colorRes: Int) = ContextCompat.getColor(this, colorRes)