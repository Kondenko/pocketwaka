package com.kondenko.pocketwaka.dagger.components

import com.kondenko.pocketwaka.dagger.PerApp
import com.kondenko.pocketwaka.dagger.modules.*
import dagger.Component

@PerApp
@Component(modules = [AppModule::class, NetModule::class, AuthModule::class])
interface AppComponent {

    fun plusMain(mainModule: MainModule): MainComponent

    fun plusAuth(): AuthComponent

    fun plusStats(statsModule: StatsModule): StatsComponent

}