package com.kondenko.pocketwaka.di.modules

import com.kondenko.pocketwaka.data.stats.converter.StatsResponseConverter
import com.kondenko.pocketwaka.domain.auth.MockGetTokenHeaderValue
import com.kondenko.pocketwaka.domain.stats.usecase.GetStatsForRange
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
}