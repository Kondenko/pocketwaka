package com.kondenko.pocketwaka.dagger.module

import com.kondenko.pocketwaka.api.services.TokenService
import com.kondenko.pocketwaka.dagger.PerViewLayer
import com.kondenko.pocketwaka.screens.activities.main.MainActivityPresenter
import com.kondenko.pocketwaka.screens.activities.main.MainActivityView
import dagger.Module
import dagger.Provides

@Module
class MainActivityPresenterModule(val view: MainActivityView) : BasePresenterModule() {

    @Provides
    @PerViewLayer
    fun providePresenter(service: TokenService): MainActivityPresenter {
        return MainActivityPresenter(service, view)
    }

}