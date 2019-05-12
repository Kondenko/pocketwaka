package com.kondenko.pocketwaka

import timber.log.Timber

open class DebugApp : App() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }

}