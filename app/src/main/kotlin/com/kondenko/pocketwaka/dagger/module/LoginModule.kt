package com.kondenko.pocketwaka.dagger.module

import com.kondenko.pocketwaka.Const
import com.kondenko.pocketwaka.R.id.view
import com.kondenko.pocketwaka.api.services.LoginService
import com.kondenko.pocketwaka.dagger.PerView
import com.kondenko.pocketwaka.screens.activities.login.LoginPresenter
import com.kondenko.pocketwaka.screens.activities.login.LoginView
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Module
class LoginModule(val view: LoginView) : BaseModule() {

    @Provides
    @PerView
    @Inject
    fun provideLoginPresenter(service: LoginService) = LoginPresenter(view, service)

}