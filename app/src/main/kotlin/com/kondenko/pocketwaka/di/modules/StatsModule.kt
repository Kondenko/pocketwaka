package com.kondenko.pocketwaka.di.modules

import com.kondenko.pocketwaka.data.android.DateFormatter
import com.kondenko.pocketwaka.data.ranges.converter.RangeResponseConverter
import com.kondenko.pocketwaka.data.ranges.repository.RangeStatsRepository
import com.kondenko.pocketwaka.data.ranges.service.RangeStatsService
import com.kondenko.pocketwaka.di.qualifiers.Api
import com.kondenko.pocketwaka.domain.auth.GetTokenHeaderValue
import com.kondenko.pocketwaka.domain.ranges.usecase.GetStatsForRanges
import com.kondenko.pocketwaka.domain.ranges.usecase.GetStatsState
import com.kondenko.pocketwaka.screens.ranges.RangesViewModel
import com.kondenko.pocketwaka.utils.ColorProvider
import com.kondenko.pocketwaka.utils.SchedulersContainer
import com.kondenko.pocketwaka.utils.encryption.StringEncryptor
import com.kondenko.pocketwaka.utils.extensions.create
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

val rangeStatsModule = module {
    single { get<Retrofit>(Api).create<RangeStatsService>() }
    single { ColorProvider(androidContext()) }
    single { DateFormatter(androidContext()) }
    single {
        RangeResponseConverter(
                context = androidContext(),
                colorProvider = get(),
                dateFormatter = get(),
                dateProvider = get()
        )
    }
    single {
        RangeStatsRepository(
                service = get(),
                dao = get()
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
        GetStatsForRanges(
                schedulers = get(),
                getTokenHeader = get() as GetTokenHeaderValue,
                rangeStatsRepository = get(),
                serverModelConverter = get<RangeResponseConverter>()
        )
    }
    factory {
        GetStatsState(
                schedulers = get(),
                getStatsForRanges = get(),
                connectivityStatusProvider = get()
        )
    }
    viewModel { (range: String?) ->
        RangesViewModel(
                range,
                getStats = get(),
                uiScheduler = get<SchedulersContainer>().uiScheduler
        )
    }
}

