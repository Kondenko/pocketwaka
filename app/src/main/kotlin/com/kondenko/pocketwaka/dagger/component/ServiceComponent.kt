package com.kondenko.pocketwaka.dagger.component

import com.kondenko.pocketwaka.dagger.module.NetModule
import com.kondenko.pocketwaka.dagger.module.ServiceModule
import com.kondenko.pocketwaka.screens.activities.login.LoginPresenter
import com.kondenko.pocketwaka.screens.activities.main.MainActivityPresenter
import com.kondenko.pocketwaka.screens.fragments.stats.FragmentStatsPresenter
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(ServiceModule::class, NetModule::class))
interface ServiceComponent {
    fun inject(presenter: LoginPresenter)
    fun inject(presenter: FragmentStatsPresenter)
    fun inject(presenter: MainActivityPresenter)
}