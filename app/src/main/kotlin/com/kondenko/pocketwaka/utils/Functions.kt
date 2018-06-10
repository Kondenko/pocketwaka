package com.kondenko.pocketwaka.utils

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import io.reactivex.Single
import java.util.concurrent.TimeUnit

/**
 * Extension functions.
 */

fun currentTimeSec() = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()).toFloat()

fun isConnectionAvailable(context: Context): Boolean {
    val service = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    return service.activeNetworkInfo?.isConnectedOrConnecting?:false
}

fun <T> T?.singleOrErrorIfNull(exception: Throwable): Single<T> = this?.let { Single.just(it) } ?: Single.error(exception)

inline fun SharedPreferences.edit(crossinline action: SharedPreferences.Editor.() -> Unit) {
    val editor = edit()
    editor.action()
    editor.apply()
}

inline fun FragmentManager.transaction(crossinline action: FragmentTransaction.() -> FragmentTransaction) {
    this.beginTransaction().action().commit()
}