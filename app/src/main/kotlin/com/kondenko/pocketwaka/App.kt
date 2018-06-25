package com.kondenko.pocketwaka

import android.app.Application
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import com.kondenko.pocketwaka.di.modulesList
import io.fabric.sdk.android.Fabric
import org.koin.android.ext.android.startKoin


open class App : Application() {

    override fun onCreate() {
        super.onCreate()
        val crashlytics = with(CrashlyticsCore.Builder()) {
            disabled(BuildConfig.DEBUG)
            build()
        }
        Fabric.with(this, Crashlytics.Builder().core(crashlytics).build())
        startKoin(this, modulesList(this))
    }

}
