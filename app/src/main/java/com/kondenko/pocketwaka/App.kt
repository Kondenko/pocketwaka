package com.kondenko.pocketwaka

import android.app.Application
import com.kondenko.pocketwaka.dagger.component.DaggerNetComponent
import com.kondenko.pocketwaka.dagger.component.NetComponent
import com.kondenko.pocketwaka.dagger.module.NetModule


class App : Application() {

    companion object {
        @JvmStatic
        lateinit var netComponent: NetComponent
    }

    override fun onCreate() {
        super.onCreate()
        netComponent = DaggerNetComponent.builder()
                .netModule(NetModule(this))
                .build()
    }

}
