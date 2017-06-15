package com.kondenko.pocketwaka.dagger.component

import com.kondenko.pocketwaka.dagger.module.AppModule
import com.kondenko.pocketwaka.dagger.module.MainActivityPresenterModule
import com.kondenko.pocketwaka.dagger.module.NetModule
import com.kondenko.pocketwaka.dagger.module.ServiceModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AppModule::class, NetModule::class, ServiceModule::class))
interface AppComponent {
    fun plus(module: MainActivityPresenterModule): MainActivityPresenterSubcomponent
}