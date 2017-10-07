package com.kondenko.pocketwaka.dagger.modules

import com.kondenko.pocketwaka.dagger.PerApp
import com.kondenko.pocketwaka.data.auth.service.AuthService
import com.kondenko.pocketwaka.screens.auth.LoginPresenter
import dagger.Module
import dagger.Provides

@Module
class AuthScreenModule {

    @Provides
    @PerApp
    fun provideLoginPresenter(service: AuthService) = LoginPresenter(service)

}