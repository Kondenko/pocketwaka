package com.kondenko.pocketwaka.analytics

import androidx.annotation.MainThread
import com.google.firebase.analytics.FirebaseAnalytics
import com.kondenko.pocketwaka.utils.WakaLog
import com.kondenko.pocketwaka.utils.extensions.className

class EventTracker(val analytics: FirebaseAnalytics) {

    @MainThread
    fun log(event: Event) {
        val tag = event
              .className(includeSuperclass = true)
              .replace("[^A-Za-z0-9_]", "") // Only leave allowed symbols
        val bundle = (event as? HasBundle)?.getBundle()
        WakaLog.d("Reporting event $tag, bundle = $bundle")
        analytics.logEvent(tag, bundle)
    }

}