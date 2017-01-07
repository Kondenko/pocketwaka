package com.kondenko.pocketwaka.dagger.component

import com.kondenko.pocketwaka.dagger.module.NetModule
import com.kondenko.pocketwaka.screens.activities.login.LoginPresenter
import dagger.Component
import javax.inject.Singleton

/**
 * A component for Wakatime authentication (requires website base URL)
 */
@Singleton
@Component(modules = arrayOf(NetModule::class))
interface LoginComponent {
    fun inject(activity: LoginPresenter)
}