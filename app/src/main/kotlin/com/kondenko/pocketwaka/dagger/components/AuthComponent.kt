package com.kondenko.pocketwaka.dagger.components

import com.kondenko.pocketwaka.dagger.PerScreen
import com.kondenko.pocketwaka.screens.auth.AuthActivity
import dagger.Subcomponent

@PerScreen
@Subcomponent()
interface AuthComponent {
    fun inject(view: AuthActivity)
}