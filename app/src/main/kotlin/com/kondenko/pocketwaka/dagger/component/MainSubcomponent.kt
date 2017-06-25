package com.kondenko.pocketwaka.dagger.component

import com.kondenko.pocketwaka.dagger.PerView
import com.kondenko.pocketwaka.dagger.module.MainModule
import com.kondenko.pocketwaka.screens.activities.main.MainActivity
import dagger.Subcomponent
import javax.inject.Singleton

@PerView
@Subcomponent(modules = arrayOf(MainModule::class))
interface MainSubcomponent : BaseSubcomponent<MainActivity, MainModule>  {
    fun injectMainSubcomponent(view: MainActivity)
}