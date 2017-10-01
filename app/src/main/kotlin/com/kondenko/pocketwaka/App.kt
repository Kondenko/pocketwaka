package com.kondenko.pocketwaka

import android.app.Application
import com.kondenko.pocketwaka.dagger.component.AppComponent
import com.kondenko.pocketwaka.dagger.component.LoginSubcomponent
import com.kondenko.pocketwaka.dagger.component.MainSubcomponent
import com.kondenko.pocketwaka.dagger.component.StatsSubcomponent
import com.kondenko.pocketwaka.dagger.module.*

open class App : Application() {

    companion object {
        @JvmStatic
        lateinit var appComponent: AppComponent
        @JvmStatic
        private var mainSubcomponent: MainSubcomponent? = null
        @JvmStatic
        private var loginSubcomponent: LoginSubcomponent? = null
        @JvmStatic
        private var statsSubcomponent: StatsSubcomponent? = null

        fun plus(module: MainModule): MainSubcomponent {
            if (mainSubcomponent == null) {
                mainSubcomponent = appComponent.plusMainSubcomponent(module)
            }
            return mainSubcomponent!!
        }

        fun plus(module: LoginModule): LoginSubcomponent {
            if (mainSubcomponent == null) {
                loginSubcomponent = appComponent.plusLoginSubcomponent(module)
            }
            return loginSubcomponent!!
        }

        fun plus(module: StatsModule): StatsSubcomponent {
            if (mainSubcomponent == null) {
                statsSubcomponent = appComponent.plusStatsSubcomponent(module)
            }
            return statsSubcomponent!!
        }

        fun clearMainComponent() {
            mainSubcomponent = null
        }

        fun clearLoginSubcomponent() {
            loginSubcomponent = null
        }

        fun clearStatsSubcomponent() {
            statsSubcomponent = null
        }
    }

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .netModule(NetModule(this))
                .build()
    }



}
