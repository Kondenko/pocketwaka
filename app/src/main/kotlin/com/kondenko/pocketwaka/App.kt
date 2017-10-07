package com.kondenko.pocketwaka

import android.app.Application
import com.kondenko.pocketwaka.dagger.components.AppComponent
import com.kondenko.pocketwaka.dagger.components.AuthComponent
import com.kondenko.pocketwaka.dagger.components.MainComponent
import com.kondenko.pocketwaka.dagger.components.StatsComponent
import com.kondenko.pocketwaka.dagger.components.DaggerAppComponent
import com.kondenko.pocketwaka.dagger.components.DaggerMainComponent
import com.kondenko.pocketwaka.dagger.components.DaggerStatsComponent
import com.kondenko.pocketwaka.dagger.components.DaggerAuthComponent
import com.kondenko.pocketwaka.dagger.modules.AppModule


open class App : Application() {

    companion object {

        @JvmStatic
        lateinit var appComponent: AppComponent
            private set

        @JvmStatic
        lateinit var mainComponent: MainComponent
            private set

        @JvmStatic
        lateinit var statsComponent: StatsComponent
            private set

        @JvmStatic
        lateinit var authComponent: AuthComponent
            private set

    }

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .build()
        mainComponent = DaggerMainComponent.builder()
                .appComponent(appComponent)
                .build()
        statsComponent = DaggerStatsComponent.builder()
                .appComponent(appComponent)
                .build()
        authComponent = DaggerAuthComponent.builder()
                .appComponent(appComponent)
                .build()
    }


}
