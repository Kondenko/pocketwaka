package com.kondenko.pocketwaka

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import com.kondenko.pocketwaka.di.TimberLogger
import com.kondenko.pocketwaka.di.koinModules
import com.kondenko.pocketwaka.utils.extensions.ifDebug
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber


open class App : Application() {

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        ifDebug {
            Timber.plant(Timber.DebugTree())
        }
        startKoin {
            logger(TimberLogger())
            androidContext(this@App)
            modules(koinModules)
        }
    }

}
