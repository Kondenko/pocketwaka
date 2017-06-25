package com.kondenko.pocketwaka.dagger.module

import com.kondenko.pocketwaka.Const
import com.kondenko.pocketwaka.api.services.LoginService
import com.kondenko.pocketwaka.dagger.PerView
import com.kondenko.pocketwaka.screens.activities.main.MainActivityPresenter
import com.kondenko.pocketwaka.screens.activities.main.MainView
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Module
class MainModule(val view: MainView) : BaseModule() {

    @Provides
    @PerView
    @Inject
    fun providePresenter(service: LoginService) = MainActivityPresenter(service, view)

}