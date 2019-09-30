package com.kondenko.pocketwaka.di.modules

import com.kondenko.pocketwaka.data.ranges.service.RangeStatsService
import com.kondenko.pocketwaka.data.stats.service.MockStatsService
import com.kondenko.pocketwaka.domain.ranges.usecase.GetStatsStateMock
import com.kondenko.pocketwaka.screens.ranges.RangesViewModel
import com.kondenko.pocketwaka.utils.SchedulersContainer
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mockStatsModule = module(override = true) {
    single<RangeStatsService> { MockStatsService(androidContext(), get()) }
    single {
        GetStatsStateMock(get(), get(), get())
    }
    viewModel { (range: String?) ->
        RangesViewModel(
                range = range,
                getStats = get<GetStatsStateMock>(),
                uiScheduler = get<SchedulersContainer>().uiScheduler
        )
    }
}