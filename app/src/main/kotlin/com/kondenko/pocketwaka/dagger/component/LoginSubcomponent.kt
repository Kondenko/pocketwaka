package com.kondenko.pocketwaka.dagger.component

import com.kondenko.pocketwaka.dagger.PerView
import com.kondenko.pocketwaka.dagger.module.LoginModule
import com.kondenko.pocketwaka.dagger.module.NetModule
import com.kondenko.pocketwaka.screens.activities.login.LoginActivity
import dagger.Subcomponent

@PerView
@Subcomponent(modules = arrayOf(NetModule::class, LoginModule::class))
interface LoginSubcomponent : BaseSubcomponent<LoginActivity, LoginModule> {
    fun injectLogin(view: LoginActivity)
}