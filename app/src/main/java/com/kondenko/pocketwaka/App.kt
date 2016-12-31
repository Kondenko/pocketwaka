package com.kondenko.pocketwaka

import android.app.Application
import android.content.Context
import com.kondenko.pocketwaka.Const
import com.kondenko.pocketwaka.dagger.component.DaggerNetComponent
import com.kondenko.pocketwaka.dagger.component.NetComponent
import com.kondenko.pocketwaka.dagger.module.AppModule
import com.kondenko.pocketwaka.dagger.module.NetModule

class App : Application() {

    companion object {
        @JvmStatic lateinit var netComponent: NetComponent

        fun get(context: Context): App
        {
            return context.applicationContext as App
        }
    }


    override fun onCreate() {
        super.onCreate()
        netComponent = DaggerNetComponent.builder()
                .appModule(AppModule(this))
                .netModule(NetModule(Const.BASE_URL))
                .build()
    }

}
