package com.kondenko.pocketwaka

import android.app.Application
import com.kondenko.pocketwaka.dagger.components.AppComponent
import com.kondenko.pocketwaka.dagger.components.AuthComponent
import com.kondenko.pocketwaka.dagger.components.MainComponent
import com.kondenko.pocketwaka.dagger.components.StatsComponent
import com.kondenko.pocketwaka.dagger.modules.AppModule
import com.kondenko.pocketwaka.dagger.modules.MainModule
import com.kondenko.pocketwaka.dagger.modules.StatsModule


open class App : Application() {

    companion object {

        lateinit var instance: App
            private set

        @JvmStatic
        lateinit var appComponent: AppComponent
            private set

    }

    private var mainComponent: MainComponent? = null

    private var statsComponent: StatsComponent? = null

    private var authComponent: AuthComponent? = null

    override fun onCreate() {
        super.onCreate()
        instance = this
        appComponent = com.kondenko.pocketwaka.dagger.components.DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .build()
    }


    fun mainComponent(): MainComponent {
        return mainComponent.initIfNull(appComponent.plusMain(MainModule())) {
            mainComponent = it
        }
    }

    fun statsComponent(): StatsComponent {
        return statsComponent.initIfNull(appComponent.plusStats(StatsModule())) {
            statsComponent = it
        }
    }

    fun authComponent(): AuthComponent {
        return authComponent.initIfNull(appComponent.plusAuth()) {
            authComponent = it
        }
    }

    fun clearMainComponent() {
        mainComponent = null
    }

    fun clearAuthComponent() {
        authComponent = null
    }

    fun clearStatsComponent() {
        statsComponent = null
    }

    private fun <T> T?.initIfNull(value: T, initFun: (T) -> Unit): T {
        if (this == null) initFun(value)
        return value
    }

}
