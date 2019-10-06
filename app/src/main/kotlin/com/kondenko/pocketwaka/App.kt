package com.kondenko.pocketwaka

import android.app.Application
import com.kondenko.pocketwaka.di.TimberLogger
import com.kondenko.pocketwaka.di.koinModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber


open class App : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
        startKoin {
            logger(TimberLogger())
            androidContext(this@App)
            modules(koinModules)
        }
    }

}
