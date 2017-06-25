package com.kondenko.pocketwaka.dagger.module

import com.kondenko.pocketwaka.Const
import com.kondenko.pocketwaka.api.services.StatsService
import com.kondenko.pocketwaka.screens.fragments.stats.StatsPresenter
import com.kondenko.pocketwaka.screens.fragments.stats.StatsView
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Module
class StatsModule(val view: StatsView) : BaseModule() {

    @Provides
    @Singleton
    @Inject
    fun provideStatsService(@Named(Const.URL_TYPE_API) retrofit: Retrofit): StatsService {
        return retrofit.create(StatsService::class.java)
    }

    @Provides
    fun provideStatsPresenter(service: StatsService) = StatsPresenter(view, service)

}