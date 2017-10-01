package com.kondenko.pocketwaka.dagger.module

import com.kondenko.pocketwaka.dagger.PerView
import com.kondenko.pocketwaka.data.auth.service.AuthService
import com.kondenko.pocketwaka.screens.main.MainActivityPresenter
import com.kondenko.pocketwaka.screens.main.MainView
import dagger.Module
import dagger.Provides
import javax.inject.Inject

@Module
class MainModule(val view: MainView) : BaseModule() {

    @Provides
    @PerView
    @Inject
    fun providePresenter(service: AuthService) = MainActivityPresenter(service, view)

}