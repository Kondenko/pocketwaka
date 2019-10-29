package com.kondenko.pocketwaka.analytics

import androidx.annotation.MainThread
import com.google.firebase.analytics.FirebaseAnalytics
import com.kondenko.pocketwaka.utils.WakaLog
import com.kondenko.pocketwaka.utils.extensions.className
import com.kondenko.pocketwaka.utils.extensions.toSnakeCase
import org.intellij.lang.annotations.RegExp

class EventTracker(val analytics: FirebaseAnalytics) {

    private val eventsTagCache = mutableMapOf<Event, String>()

    @MainThread
    fun log(event: Event) {
        val tag = eventsTagCache.getOrPut(event) {
            event
                  .className(includeSuperclass = event::class.java.superclass != Event::class.java)
                  .replace("[^A-Za-z0-9_]", "") // Only leave allowed symbols
                  .toSnakeCase()
        }
        val bundle = try {
            (event as? HasBundle)?.getBundle()
        } catch (e: IllegalArgumentException) {
            WakaLog.w("Couldn't create a bundle for $event")
            null
        }
        analytics.logEvent(tag, bundle)
    }

}