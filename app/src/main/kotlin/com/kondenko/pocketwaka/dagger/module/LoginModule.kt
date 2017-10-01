package com.kondenko.pocketwaka.dagger.module

import com.kondenko.pocketwaka.dagger.PerView
import com.kondenko.pocketwaka.data.auth.service.AuthService
import com.kondenko.pocketwaka.screens.auth.LoginPresenter
import com.kondenko.pocketwaka.screens.auth.LoginView
import dagger.Module
import dagger.Provides
import javax.inject.Inject

@Module
class LoginModule(val view: LoginView) : BaseModule() {

    @Provides
    @PerView
    @Inject
    fun provideLoginPresenter(service: AuthService) = LoginPresenter(view, service)

}