package com.kondenko.pocketwaka.dagger.module

import com.kondenko.pocketwaka.api.services.StatsService
import com.kondenko.pocketwaka.api.services.TokenService
import com.nhaarman.mockito_kotlin.mock
import dagger.Module
import dagger.Provides
import javax.inject.Inject
import javax.inject.Singleton

@Module
class TestServiceModule {

    @Provides
    @Singleton
    fun provideTokenService(): TokenService {
        return mock()
    }

    @Provides
    @Singleton
    @Inject
    fun provideStatsService(): StatsService {
        return mock()
    }

}