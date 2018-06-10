package com.kondenko.pocketwaka.dagger.components

import com.kondenko.pocketwaka.dagger.PerApp
import com.kondenko.pocketwaka.dagger.modules.MainModule
import com.kondenko.pocketwaka.screens.main.MainActivity
import dagger.Component

@PerApp
@Component(modules = arrayOf(MainModule::class), dependencies = arrayOf(AppComponent::class))
interface MainComponent {
    fun inject(view: MainActivity)
}