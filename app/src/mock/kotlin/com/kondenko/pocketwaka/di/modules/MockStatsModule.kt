package com.kondenko.pocketwaka.di.modules

import com.kondenko.pocketwaka.data.stats.converter.StatsResponseConverter
import com.kondenko.pocketwaka.di.qualifiers.Scheduler.Ui
import com.kondenko.pocketwaka.domain.auth.MockGetTokenHeaderValue
import com.kondenko.pocketwaka.domain.stats.usecase.GetStatsForRange
import com.kondenko.pocketwaka.domain.stats.usecase.GetStatsStateMock
import com.kondenko.pocketwaka.screens.stats.StatsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mockStatsModule = module(override = true) {
    factory {
        GetStatsForRange(
              schedulers = get(),
              getTokenHeader = get<MockGetTokenHeaderValue>(),
              statsRepository = get(),
              serverModelConverter = get<StatsResponseConverter>()
        )
    }
    factory { GetStatsStateMock(get()) }
    viewModel { (range: String) ->
        StatsViewModel(
              range,
              get<GetStatsStateMock>(),
              get(Ui)
        )
    }
}