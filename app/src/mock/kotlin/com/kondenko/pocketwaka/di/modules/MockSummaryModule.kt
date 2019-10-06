package com.kondenko.pocketwaka.di.modules

import com.kondenko.pocketwaka.data.summary.service.MockSummaryService
import com.kondenko.pocketwaka.data.summary.service.SummaryService
import com.kondenko.pocketwaka.domain.daily.usecase.GetSummary
import com.kondenko.pocketwaka.domain.daily.usecase.GetSummaryStateMock
import com.kondenko.pocketwaka.screens.daily.SummaryViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mockSummaryModule = module(override = true) {
    single<SummaryService> { MockSummaryService(androidContext(), get()) }
    single {
        GetSummaryStateMock(
                schedulers = get(),
                useCase = get<GetSummary>(),
                connectivityStatusProvider = get()
        )
    }
    viewModel {
        SummaryViewModel(
                getDefaultSummaryRange = get(),
                getSummaryState = get<GetSummaryStateMock>()
        )
    }

}