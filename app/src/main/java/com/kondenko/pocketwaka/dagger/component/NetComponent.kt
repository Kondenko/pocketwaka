package com.kondenko.pocketwaka.dagger.component

import com.kondenko.pocketwaka.dagger.module.NetModule
import com.kondenko.pocketwaka.screens.activities.login.LoginPresenter
import com.kondenko.pocketwaka.screens.fragments.stats.FragmentStatsPresenter
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(NetModule::class))
interface NetComponent {
    fun inject(activity: FragmentStatsPresenter)
    fun inject(activity: LoginPresenter)
}