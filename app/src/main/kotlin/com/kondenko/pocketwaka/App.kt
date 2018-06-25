package com.kondenko.pocketwaka

import android.app.Application
import com.kondenko.pocketwaka.di.modulesList
import org.koin.android.ext.android.startKoin


open class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin(this, modulesList(this))
    }

}
