package com.kondenko.pocketwaka.utils.extensions

import android.content.Context
import android.os.Build
import android.util.TypedValue
import java.util.*

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