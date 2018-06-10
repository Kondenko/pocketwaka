package com.kondenko.pocketwaka.dagger.components

import com.kondenko.pocketwaka.dagger.PerScreen
import com.kondenko.pocketwaka.dagger.modules.MainModule
import com.kondenko.pocketwaka.screens.main.MainActivity
import dagger.Subcomponent

@PerScreen
@Subcomponent(modules = [MainModule::class])
interface MainComponent {
    fun inject(view: MainActivity)
}