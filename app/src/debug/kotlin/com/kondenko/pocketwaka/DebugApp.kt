package com.kondenko.pocketwaka

import com.facebook.stetho.Stetho
import timber.log.Timber

open class DebugApp : App() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        Stetho.initializeWithDefaults(this)
    }

}