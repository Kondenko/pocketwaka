package com.kondenko.pocketwaka.dagger.module

import android.content.Context
import com.kondenko.pocketwaka.Const
import com.kondenko.pocketwaka.data.auth.service.AuthService
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
    fun provideTokenService(@Named(Const.URL_TYPE_AUTH) retrofit: Retrofit): AuthService {
        return retrofit.create(AuthService::class.java)
    }

}