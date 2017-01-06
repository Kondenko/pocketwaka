package com.kondenko.pocketwaka.dagger.component

import com.kondenko.pocketwaka.dagger.module.NetModule
import com.kondenko.pocketwaka.screens.fragments.stats.FragmentStatsPresenter
import dagger.Component
import javax.inject.Singleton

/**
 * A component for Wakatime API calls (requires API base URL)
 */
@Singleton
@Component(modules = arrayOf(NetModule::class))
interface ApiComponent {
    fun inject(activity: FragmentStatsPresenter)
}