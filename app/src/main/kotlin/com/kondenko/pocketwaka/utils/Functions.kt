package com.kondenko.pocketwaka.utils

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import java.util.concurrent.TimeUnit

/**
 * Extension functions.
 */

fun currentTimeSec() = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()).toFloat()

fun isConnectionAvailable(context: Context): Boolean {
    val service = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    return service.activeNetworkInfo != null && service.activeNetworkInfo.isConnectedOrConnecting
}

inline fun SharedPreferences.edit(crossinline action: SharedPreferences.Editor.() -> Unit) {
    val editor = edit()
    editor.action()
    editor.apply()
}
