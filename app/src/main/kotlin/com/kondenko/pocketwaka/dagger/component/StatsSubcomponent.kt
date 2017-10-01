package com.kondenko.pocketwaka.dagger.component

import com.kondenko.pocketwaka.dagger.PerView
import com.kondenko.pocketwaka.dagger.module.StatsModule
import com.kondenko.pocketwaka.screens.stats.FragmentStats
import dagger.Subcomponent

@PerView
@Subcomponent(modules = arrayOf(StatsModule::class))
interface StatsSubcomponent : BaseSubcomponent<FragmentStats, StatsModule> {
    override fun inject(view: FragmentStats)
}