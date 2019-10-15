package com.kondenko.pocketwaka.di.modules

import com.kondenko.pocketwaka.data.summary.service.MockSummaryService
import com.kondenko.pocketwaka.data.summary.service.SummaryService
import com.kondenko.pocketwaka.di.qualifiers.Scheduler
import com.kondenko.pocketwaka.domain.summary.usecase.GetSummaryStateMock
import com.kondenko.pocketwaka.screens.summary.SummaryViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mockSummaryModule = module(override = true) {
    single<SummaryService> { MockSummaryService(androidContext(), get()) }
    single {
        GetSummaryStateMock(get())
    }
    viewModel {
        SummaryViewModel(
              uiScheduler = get(Scheduler.Ui),
              getDefaultSummaryRange = get(),
              getSummaryState = get<GetSummaryStateMock>()
        )
    }

}