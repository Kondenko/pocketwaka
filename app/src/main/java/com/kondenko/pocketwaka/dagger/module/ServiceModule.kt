package com.kondenko.pocketwaka.dagger.module

import com.kondenko.pocketwaka.Const
import com.kondenko.pocketwaka.api.services.StatsService
import com.kondenko.pocketwaka.api.services.TokenService
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Module
class ServiceModule {

    @Provides
    @Singleton
    @Inject
    fun provideTokenService(@Named(Const.URL_TYPE_AUTH) retrofit: Retrofit): TokenService {
        return retrofit.create(TokenService::class.java)
    }

    @Provides
    @Singleton
    @Inject
    fun provideStatsService(@Named(Const.URL_TYPE_API) retrofit: Retrofit): StatsService {
        return retrofit.create(StatsService::class.java)
    }

}