package com.kondenko.pocketwaka.dagger.module

import com.kondenko.pocketwaka.data.stats.service.StatsService
import com.kondenko.pocketwaka.data.auth.service.AuthService
import com.nhaarman.mockito_kotlin.mock
import dagger.Module
import dagger.Provides
import javax.inject.Inject
import javax.inject.Singleton

@Module
class TestServiceModule {

    @Provides
    @Singleton
    fun provideTokenService(): AuthService {
        return mock()
    }

    @Provides
    @Singleton
    @Inject
    fun provideStatsService(): StatsService {
        return mock()
    }

}