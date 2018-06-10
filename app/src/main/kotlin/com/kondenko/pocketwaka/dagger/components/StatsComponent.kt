package com.kondenko.pocketwaka.dagger.components

import com.kondenko.pocketwaka.dagger.PerScreen
import com.kondenko.pocketwaka.dagger.modules.StatsModule
import com.kondenko.pocketwaka.screens.stats.FragmentStatsTab
import dagger.Subcomponent

@PerScreen
@Subcomponent(modules = [(StatsModule::class)])
interface StatsComponent {
    fun inject(view: FragmentStatsTab)
}