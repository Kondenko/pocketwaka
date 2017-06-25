package com.kondenko.pocketwaka.dagger.component

import com.kondenko.pocketwaka.dagger.PerView
import com.kondenko.pocketwaka.dagger.module.NetModule
import com.kondenko.pocketwaka.dagger.module.StatsModule
import com.kondenko.pocketwaka.screens.fragments.stats.FragmentStats
import dagger.Subcomponent

@PerView
@Subcomponent(modules = arrayOf(NetModule::class, StatsModule::class))
interface StatsSubcomponent : BaseSubcomponent<FragmentStats, StatsModule> {
    fun injectStats(view: FragmentStats)
}