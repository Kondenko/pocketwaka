package com.kondenko.pocketwaka.dagger.component

import com.kondenko.pocketwaka.dagger.module.*
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AppModule::class, NetModule::class))
interface AppComponent {
    fun plusMainSubcomponent(module: MainModule): MainSubcomponent
    fun plusStatsSubcomponent(module: StatsModule): StatsSubcomponent
    fun plusLoginSubcomponent(module: LoginModule): LoginSubcomponent
}