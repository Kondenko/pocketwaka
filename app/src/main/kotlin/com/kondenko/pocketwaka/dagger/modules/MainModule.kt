package com.kondenko.pocketwaka.dagger.modules

import com.kondenko.pocketwaka.dagger.PerApp
import com.kondenko.pocketwaka.data.auth.service.AuthService
import com.kondenko.pocketwaka.screens.main.MainActivityPresenter
import dagger.Module
import dagger.Provides

@Module
class MainModule {

    @Provides
    @PerApp
    fun providePresenter(service: AuthService) = MainActivityPresenter(service)

}