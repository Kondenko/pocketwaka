package com.kondenko.pocketwaka.di.modules

import android.content.Context
import com.kondenko.pocketwaka.data.stats.repository.StatsRepository
import com.kondenko.pocketwaka.data.stats.service.StatsService
import com.kondenko.pocketwaka.di.Api
import com.kondenko.pocketwaka.domain.auth.GetTokenHeaderValue
import com.kondenko.pocketwaka.domain.stats.GetStats
import com.kondenko.pocketwaka.screens.stats.StatsPresenter
import com.kondenko.pocketwaka.utils.ColorProvider
import org.koin.dsl.module.applicationContext
import retrofit2.Retrofit

object StatsModule {
    fun create(context: Context) = applicationContext {
        bean { get<Retrofit>(Api).create(StatsService::class.java) }
        bean { StatsRepository(context, get()) }
        bean { ColorProvider(context) }
        factory { GetTokenHeaderValue(get(), get(), get()) }
        factory { GetStats(get(), get(), get(), get() as GetTokenHeaderValue, get()) }
        factory { StatsPresenter(get() as GetStats) }
    }
}

