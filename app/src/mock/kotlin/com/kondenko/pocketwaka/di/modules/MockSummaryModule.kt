package com.kondenko.pocketwaka.di.modules

import com.kondenko.pocketwaka.data.summary.converters.SummaryResponseConverter
import com.kondenko.pocketwaka.data.summary.converters.TimeTrackedConverter
import com.kondenko.pocketwaka.data.summary.repository.SummaryRepository
import com.kondenko.pocketwaka.data.summary.service.MockSummaryService
import com.kondenko.pocketwaka.di.qualifiers.Scheduler
import com.kondenko.pocketwaka.domain.auth.MockGetTokenHeaderValue
import com.kondenko.pocketwaka.domain.summary.usecase.FetchProjects
import com.kondenko.pocketwaka.domain.summary.usecase.GetAverage
import com.kondenko.pocketwaka.domain.summary.usecase.GetSummary
import com.kondenko.pocketwaka.domain.summary.usecase.GetSummaryStateMock
import com.kondenko.pocketwaka.screens.summary.SummaryViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mockSummaryModule = module(override = true) {
    single { MockSummaryService(androidContext(), get()) }
    single {
        GetAverage(
              schedulersContainer = get(),
              statsRepository = get(),
              getTokenHeaderValue = get<MockGetTokenHeaderValue>()
        )
    }
    single {
        GetSummary(
              schedulers = get(),
              summaryRepository = get<SummaryRepository>(),
              getTokenHeader = get<MockGetTokenHeaderValue>(),
              dateFormatter = get(),
              summaryResponseConverter = get<SummaryResponseConverter>(),
              timeTrackedConverter = get<TimeTrackedConverter>(),
              fetchProjects = get<FetchProjects>()
        )
    }
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