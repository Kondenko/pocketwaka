package com.kondenko.pocketwaka.di.modules

import com.kondenko.pocketwaka.data.MockSummaryService
import com.kondenko.pocketwaka.data.summary.service.SummaryService
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val mockSummaryModule = module {
    single(override = true) { MockSummaryService(androidContext(), get()) as SummaryService }
}