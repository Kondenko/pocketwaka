package com.kondenko.pocketwaka.dagger.components

import com.kondenko.pocketwaka.dagger.PerApp
import com.kondenko.pocketwaka.dagger.modules.StatsModule
import com.kondenko.pocketwaka.screens.stats.FragmentStats
import dagger.Component

@PerApp
@Component(modules = arrayOf(StatsModule::class), dependencies = arrayOf(AppComponent::class))
interface StatsComponent {
    fun inject(view: FragmentStats)
}