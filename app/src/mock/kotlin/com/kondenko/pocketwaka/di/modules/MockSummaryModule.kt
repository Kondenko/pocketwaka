package com.kondenko.pocketwaka.di.modules

import com.kondenko.pocketwaka.data.summary.converters.SummaryResponseConverter
import com.kondenko.pocketwaka.data.summary.converters.TimeTrackedConverter
import com.kondenko.pocketwaka.data.summary.repository.SummaryRepository
import com.kondenko.pocketwaka.di.qualifiers.Scheduler
import com.kondenko.pocketwaka.domain.auth.MockGetTokenHeaderValue
import com.kondenko.pocketwaka.domain.summary.usecase.FetchBranchesAndCommits
import com.kondenko.pocketwaka.domain.summary.usecase.GetAverage
import com.kondenko.pocketwaka.domain.summary.usecase.GetSummary
import com.kondenko.pocketwaka.domain.summary.usecase.GetSummaryStateMock
import com.kondenko.pocketwaka.screens.summary.SummaryViewModel
import com.kondenko.pocketwaka.utils.date.DateRange
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mockSummaryModule = module(override = true) {
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
              fetchBranchesAndCommits = get<FetchBranchesAndCommits>()
        )
    }
    single {
        GetSummaryStateMock(get(), get())
    }
    viewModel {
        SummaryViewModel(
              range = DateRange.PredefinedRange.Today.range,
              uiScheduler = get(Scheduler.Ui),
              getSummaryState = get<GetSummaryStateMock>()
        )
    }
}