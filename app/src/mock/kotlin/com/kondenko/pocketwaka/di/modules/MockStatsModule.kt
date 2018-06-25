package com.kondenko.pocketwaka.di.modules

import android.content.Context
import com.kondenko.pocketwaka.data.stats.service.MockStatsService
import com.kondenko.pocketwaka.data.stats.service.StatsService
import org.koin.dsl.module.applicationContext

object MockStatsModule {
    fun create(context: Context) = applicationContext {
        bean { MockStatsService(context, get()) as StatsService }
    }
}