package com.kondenko.pocketwaka.di

import com.kondenko.pocketwaka.di.modules.*

val koinModules = listOf(
        appModule,
        netModule,
        persistenceModule,
        firebaseModule,
        mainModule,
        authModule,
        summaryModule,
        rangeStatsModule,
        menuModule,
        commitsModule,
        durationsModule
)
