package com.kondenko.pocketwaka

import android.app.Application
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import com.kondenko.pocketwaka.di.TimberLogger
import com.kondenko.pocketwaka.di.koinModules
import io.fabric.sdk.android.Fabric
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber


open class App : Application() {

    override fun onCreate() {
        super.onCreate()
        val isDebug = BuildConfig.DEBUG

        val crashlytics = with(CrashlyticsCore.Builder()) {
            disabled(isDebug)
            build()
        }
        Fabric.with(this, Crashlytics.Builder().core(crashlytics).build())

        if (isDebug) {
            Timber.plant(Timber.DebugTree())
        }

        startKoin {
            logger(TimberLogger())
            androidContext(this@App)
            modules(koinModules)
        }
    }

}
