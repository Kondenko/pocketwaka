package com.kondenko.pocketwaka.di.modules

import android.content.Context
import com.kondenko.pocketwaka.data.android.DateFormatter
import com.kondenko.pocketwaka.data.ranges.converter.RangeDomainModelConverter
import com.kondenko.pocketwaka.data.ranges.converter.RangeResponseConverter
import com.kondenko.pocketwaka.data.ranges.repository.StatsRepository
import com.kondenko.pocketwaka.data.ranges.service.StatsService
import com.kondenko.pocketwaka.di.Api
import com.kondenko.pocketwaka.domain.auth.GetTokenHeaderValue
import com.kondenko.pocketwaka.domain.ranges.usecase.FetchStats
import com.kondenko.pocketwaka.domain.ranges.usecase.GetStatsState
import com.kondenko.pocketwaka.screens.ranges.RangesViewModel
import com.kondenko.pocketwaka.utils.ColorProvider
import com.kondenko.pocketwaka.utils.SchedulersContainer
import com.kondenko.pocketwaka.utils.encryption.StringEncryptor
import com.kondenko.pocketwaka.utils.extensions.create
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

object StatsModule {
    fun create(context: Context) = module {
        single { get<Retrofit>(Api).create<StatsService>() }
        single { ColorProvider(context) }
        single { DateFormatter(context) }
        single {
            RangeResponseConverter(
                    context = context,
                    colorProvider = get(),
                    dateFormatter = get(),
                    timeProvider = get()
            )
        }
        single {
            RangeDomainModelConverter()
        }
        single {
            StatsRepository(
                    service = get(),
                    dao = get(),
                    serviceResponseConverter = get<RangeResponseConverter>(),
                    dtoConverter = get<RangeDomainModelConverter>()
            )
        }
        factory {
            GetTokenHeaderValue(
                    schedulers = get(),
                    stringEncryptor = get<StringEncryptor>(),
                    accessTokenRepository = get()
            )
        }
        factory {
            FetchStats(
                    schedulers = get(),
                    getTokenHeader = get() as GetTokenHeaderValue,
                    statsRepository = get()
            )
        }
        factory {
            GetStatsState(
                    schedulers = get(),
                    fetchStats = get(),
                    connectivityStatusProvider = get()
            )
        }
        viewModel { (range: String?) -> RangesViewModel(range, get() as GetStatsState, get<SchedulersContainer>().uiScheduler) }
    }

}

