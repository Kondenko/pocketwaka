package com.kondenko.pocketwaka.di.modules

import android.content.Context
import com.kondenko.pocketwaka.data.android.DateFormatter
import com.kondenko.pocketwaka.data.stats.repository.StatsRepository
import com.kondenko.pocketwaka.data.stats.service.StatsService
import com.kondenko.pocketwaka.di.Api
import com.kondenko.pocketwaka.domain.auth.GetTokenHeaderValue
import com.kondenko.pocketwaka.domain.stats.FetchStats
import com.kondenko.pocketwaka.domain.stats.GetSkeletonPlaceholderData
import com.kondenko.pocketwaka.domain.stats.GetStatsState
import com.kondenko.pocketwaka.screens.stats.StatsViewModel
import com.kondenko.pocketwaka.utils.ColorProvider
import com.kondenko.pocketwaka.utils.extensions.create
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

object StatsModule {
    fun create(context: Context) = module {
        single { get<Retrofit>(Api).create<StatsService>() }
        single { StatsRepository(context, get()) }
        single { ColorProvider(context) }
        single { DateFormatter(context) }
        single { GetSkeletonPlaceholderData(get()) }
        factory {
            GetTokenHeaderValue(
                    schedulers = get(),
                    encryptor = get(),
                    accessTokenRepository = get()
            )
        }
        factory {
            FetchStats(
                    schedulers = get(),
                    timeProvider = get(),
                    colorProvider = get(),
                    dateFormatter = get(),
                    getTokenHeader = get() as GetTokenHeaderValue,
                    statsRepository = get()
            )
        }
        factory {
            GetStatsState(
                    schedulers = get(),
                    getSkeletonPlaceholderData = get(),
                    fetchStats = get()
            )
        }
        viewModel { (range: String) -> StatsViewModel(range, get() as GetStatsState) }
    }

}

