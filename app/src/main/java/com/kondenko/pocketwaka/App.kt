package com.kondenko.pocketwaka

import android.app.Application
import android.content.Context
import com.kondenko.pocketwaka.dagger.component.ApiComponent
import com.kondenko.pocketwaka.dagger.component.DaggerApiComponent
import com.kondenko.pocketwaka.dagger.component.DaggerLoginComponent
import com.kondenko.pocketwaka.dagger.component.LoginComponent
import com.kondenko.pocketwaka.dagger.module.NetModule


class App : Application() {

    companion object {
        @JvmStatic lateinit var loginComponent: LoginComponent
        @JvmStatic lateinit var apiComponent: ApiComponent

        fun get(context: Context): App {
            return context.applicationContext as App
        }
    }


    override fun onCreate() {
        super.onCreate()
        loginComponent = DaggerLoginComponent.builder()
                .netModule(NetModule(Const.BASE_URL))
                .build()
        apiComponent = DaggerApiComponent.builder()
                .netModule(NetModule(Const.API_URL))
                .build()
    }

}
