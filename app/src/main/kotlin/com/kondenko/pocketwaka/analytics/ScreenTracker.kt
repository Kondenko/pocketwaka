package com.kondenko.pocketwaka.analytics

import android.app.Activity
import androidx.annotation.MainThread
import com.google.firebase.analytics.FirebaseAnalytics
import com.kondenko.pocketwaka.utils.WakaLog

class ScreenTracker(val analytics: FirebaseAnalytics) {

    @MainThread
    fun log(activity: Activity?, screen: Screen) {
        activity?.let {
            analytics.setCurrentScreen(activity, screen.name, screen::class.java.toString())
        } ?: WakaLog.w("$screen wasn't logged because its Activity is null")
    }

}