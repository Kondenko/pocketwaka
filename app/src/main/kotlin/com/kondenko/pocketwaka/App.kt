package com.kondenko.pocketwaka

import android.app.Application
import com.kondenko.pocketwaka.dagger.component.AppComponent
import com.kondenko.pocketwaka.dagger.component.MainActivityPresenterSubcomponent
import com.kondenko.pocketwaka.dagger.component.ServiceComponent
import com.kondenko.pocketwaka.dagger.component.DaggerAppComponent
import com.kondenko.pocketwaka.dagger.component.DaggerServiceComponent
import com.kondenko.pocketwaka.dagger.module.AppModule
import com.kondenko.pocketwaka.dagger.module.MainActivityPresenterModule
import com.kondenko.pocketwaka.dagger.module.NetModule
import com.kondenko.pocketwaka.dagger.module.ServiceModule

open class App : Application() {

    companion object {
        @JvmStatic
        lateinit var appComponent: AppComponent
        @JvmStatic
        lateinit var serviceComponent: ServiceComponent
        @JvmStatic
        private var mainActivityPresenterSubcomponent: MainActivityPresenterSubcomponent? = null

        fun plus(module: MainActivityPresenterModule): MainActivityPresenterSubcomponent {
            if (mainActivityPresenterSubcomponent == null) {
                mainActivityPresenterSubcomponent = appComponent.plus(module)
            }
            return mainActivityPresenterSubcomponent!!
        }

        fun clearMainActivityComponent() {
            mainActivityPresenterSubcomponent = null
        }
    }

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .netModule(NetModule(this))
                .serviceModule(ServiceModule())
                .build()
        serviceComponent = DaggerServiceComponent.builder()
                .netModule(NetModule(this))
                .serviceModule(ServiceModule())
                .build()
    }



}
