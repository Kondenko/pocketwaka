package com.kondenko.pocketwaka.di.modules

import com.kondenko.pocketwaka.data.stats.service.MockStatsService
import com.kondenko.pocketwaka.data.stats.service.RangeStatsService
import com.kondenko.pocketwaka.domain.stats.usecase.GetStatsForRange
import com.kondenko.pocketwaka.domain.stats.usecase.GetStatsStateMock
import com.kondenko.pocketwaka.screens.stats.StatsViewModel
import com.kondenko.pocketwaka.utils.SchedulersContainer
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mockStatsModule = module(override = true) {
    single<RangeStatsService> { MockStatsService(androidContext(), get()) }
    single {
        GetStatsStateMock(
                schedulers = get(),
                useCase = get<GetStatsForRange>(),
                connectivityStatusProvider = get()
        )
    }
    viewModel { (range: String?) ->
        StatsViewModel(
                range = range,
                getStats = get<GetStatsStateMock>(),
                uiScheduler = get<SchedulersContainer>().uiScheduler
        )
    }
}