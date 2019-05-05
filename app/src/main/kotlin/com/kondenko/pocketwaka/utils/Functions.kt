package com.kondenko.pocketwaka.utils

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Build
import android.util.TypedValue
import android.view.View
import androidx.core.view.ViewCompat
import androidx.fragment.app.FragmentManager
import com.crashlytics.android.Crashlytics
import io.reactivex.Single
import timber.log.Timber

fun isConnectionAvailable(context: Context): Boolean {
    val service = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    return service.activeNetworkInfo?.isConnectedOrConnecting?:false
}

fun <T> T?.singleOrErrorIfNull(exception: Throwable): Single<T> = this?.let { Single.just(it) } ?: Single.error(exception)

/**
 * Extension functions.
 */

inline fun SharedPreferences.edit(crossinline action: SharedPreferences.Editor.() -> Unit) {
    val editor = edit()
    editor.action()
    editor.apply()
}

inline fun FragmentManager.transaction(crossinline action: androidx.fragment.app.FragmentTransaction.() -> androidx.fragment.app.FragmentTransaction) {
    this.beginTransaction().action().commit()
}

/**
 * Update a view's dimension so it matches the device's density
 */
fun Context.adjustForDensity(value: Float) = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, resources.displayMetrics)

/**
 * Update a view's dimension so it matches the device's density
 */
fun Context.adjustForDensity(value: Int) = adjustForDensity(value.toFloat())

fun View.elevation(elevation: Float) {
    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) ViewCompat.setElevation(this, elevation)
    else this.elevation = elevation
}

/**
 * Prints log output and sends a report to crashlyics about the given exception
 */
fun Throwable.report(message: String? = null) {
    Timber.e(this, message?:this.message)
    Crashlytics.logException(this)
}