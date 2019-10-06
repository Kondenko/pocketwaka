package com.kondenko.pocketwaka.di

import com.kondenko.pocketwaka.di.modules.mockAppModule
import com.kondenko.pocketwaka.di.modules.mockStatsModule
import com.kondenko.pocketwaka.di.modules.mockSummaryModule

val mockModules = listOf(
        mockAppModule,
        mockStatsModule,
        mockSummaryModule
)