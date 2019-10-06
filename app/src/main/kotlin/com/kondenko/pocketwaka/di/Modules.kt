package com.kondenko.pocketwaka.di

import com.kondenko.pocketwaka.di.modules.*

val koinModules = listOf(
        appModule,
        netModule,
        persistenceModule,
        mainModule,
        authModule,
        summaryModule,
        rangeStatsModule,
        menuModule,
        commitsModule,
        durationsModule
)
