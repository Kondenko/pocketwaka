package com.kondenko.pocketwaka.dagger.module

import android.content.Context
import com.kondenko.pocketwaka.Const
import com.kondenko.pocketwaka.api.services.LoginService
import com.kondenko.pocketwaka.dagger.PerView
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Module
class AppModule(val context: Context) {

    @Provides
    @Singleton
    fun provideContext() = context

    @Provides
    @Singleton
    @Inject
    fun provideTokenService(@Named(Const.URL_TYPE_AUTH) retrofit: Retrofit): LoginService {
        return retrofit.create(LoginService::class.java)
    }

}