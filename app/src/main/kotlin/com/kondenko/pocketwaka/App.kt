package com.kondenko.pocketwaka

import android.app.Application
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import com.kondenko.pocketwaka.di.TimberLogger
import com.kondenko.pocketwaka.di.getModuleList
import io.fabric.sdk.android.Fabric
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin


open class App : Application() {

    override fun onCreate() {
        super.onCreate()
        val crashlytics = with(CrashlyticsCore.Builder()) {
            disabled(BuildConfig.DEBUG)
            build()
        }
        Fabric.with(this, Crashlytics.Builder().core(crashlytics).build())
        startKoin {
            logger(TimberLogger())
            androidContext(this@App)
            modules(getModuleList(this@App))
        }
    }

}
