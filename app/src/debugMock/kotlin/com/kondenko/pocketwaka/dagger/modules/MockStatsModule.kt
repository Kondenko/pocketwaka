package com.kondenko.pocketwaka.dagger.modules

import com.kondenko.pocketwaka.dagger.PerScreen
import com.kondenko.pocketwaka.dagger.qualifiers.Api
import com.kondenko.pocketwaka.data.stats.service.MockStatsService
import com.kondenko.pocketwaka.data.stats.service.StatsService
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class MockStatsModule : StatsModule() {

    @Provides
    @PerScreen
    override fun provideStatsService(@Api retrofit: Retrofit): StatsService {
        return MockStatsService()
    }

}