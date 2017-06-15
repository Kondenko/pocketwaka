package com.kondenko.pocketwaka.dagger.component

import com.kondenko.pocketwaka.dagger.PerViewLayer
import com.kondenko.pocketwaka.dagger.module.MainActivityPresenterModule
import com.kondenko.pocketwaka.screens.activities.main.MainActivity
import dagger.Subcomponent

@PerViewLayer
@Subcomponent(modules = arrayOf(MainActivityPresenterModule::class))
interface MainActivityPresenterSubcomponent : BasePresenterSubcomponent<MainActivity, MainActivityPresenterModule>  {
    override fun inject(view: MainActivity)
}