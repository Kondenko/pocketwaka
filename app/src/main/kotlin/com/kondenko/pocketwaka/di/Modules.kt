package com.kondenko.pocketwaka.di

import com.kondenko.pocketwaka.di.modules.*

val koinModules = listOf(
      appModule,
      netModule,
      persistenceModule,
      firebaseModule,
      analyticsModule,
      mainModule,
      menuModule,
      authModule,
      summaryModule,
      statsModule,
      commitsModule,
      durationsModule
)
