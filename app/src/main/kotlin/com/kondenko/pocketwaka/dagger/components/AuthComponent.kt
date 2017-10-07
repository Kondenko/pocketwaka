package com.kondenko.pocketwaka.dagger.components

import com.kondenko.pocketwaka.dagger.PerApp
import com.kondenko.pocketwaka.dagger.modules.AuthScreenModule
import com.kondenko.pocketwaka.screens.auth.AuthActivity
import dagger.Component

@PerApp
@Component(modules = arrayOf(AuthScreenModule::class), dependencies = arrayOf(AppComponent::class))
interface AuthComponent {
    fun inject(view: AuthActivity)
}