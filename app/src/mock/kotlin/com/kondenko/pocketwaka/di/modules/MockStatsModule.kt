package com.kondenko.pocketwaka.di.modules

import com.kondenko.pocketwaka.data.ranges.service.RangeStatsService
import com.kondenko.pocketwaka.data.stats.service.MockStatsService
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val mockStatsModule = module {
    single(override=true) { MockStatsService(androidContext(), get()) as RangeStatsService }
}