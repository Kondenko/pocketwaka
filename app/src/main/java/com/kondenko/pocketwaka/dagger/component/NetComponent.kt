package com.kondenko.pocketwaka.dagger.component

import com.kondenko.pocketwaka.dagger.module.NetModule
import com.kondenko.pocketwaka.screens.login.LoginPresenterImpl
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(NetModule::class))
interface NetComponent {
    fun inject(activity: LoginPresenterImpl)
}