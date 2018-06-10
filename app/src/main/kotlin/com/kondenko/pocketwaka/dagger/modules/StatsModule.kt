package com.kondenko.pocketwaka.dagger.modules

import android.content.Context
import com.kondenko.pocketwaka.dagger.PerApp
import com.kondenko.pocketwaka.dagger.qualifiers.Api
import com.kondenko.pocketwaka.data.stats.service.StatsService
import com.kondenko.pocketwaka.utils.ColorProvider
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class StatsModule {

    @Provides
    @PerApp
    fun provideColorProvider(context: Context) = ColorProvider(context)

    @Provides
    @PerApp
    fun provideStatsService(@Api retrofit: Retrofit): StatsService {
        return retrofit.create(StatsService::class.java)
    }

}