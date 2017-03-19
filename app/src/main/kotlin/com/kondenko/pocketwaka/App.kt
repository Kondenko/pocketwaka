package com.kondenko.pocketwaka

import android.app.Application
import com.kondenko.pocketwaka.dagger.component.DaggerServiceComponent
import com.kondenko.pocketwaka.dagger.component.ServiceComponent
import com.kondenko.pocketwaka.dagger.module.NetModule
import com.kondenko.pocketwaka.dagger.module.ServiceModule

open class App : Application() {

    companion object {
        @JvmStatic
        lateinit var serviceComponent: ServiceComponent
    }

    override fun onCreate() {
        super.onCreate()
        serviceComponent = DaggerServiceComponent.builder()
                .netModule(NetModule(this))
                .serviceModule(ServiceModule())
                .build()
    }

}
