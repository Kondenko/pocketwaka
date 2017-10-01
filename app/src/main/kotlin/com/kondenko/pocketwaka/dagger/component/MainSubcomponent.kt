package com.kondenko.pocketwaka.dagger.component

import com.kondenko.pocketwaka.dagger.PerView
import com.kondenko.pocketwaka.dagger.module.MainModule
import com.kondenko.pocketwaka.screens.main.MainActivity
import dagger.Subcomponent

@PerView
@Subcomponent(modules = arrayOf(MainModule::class))
interface MainSubcomponent : BaseSubcomponent<MainActivity, MainModule>  {
    fun injectMainSubcomponent(view: MainActivity)
}