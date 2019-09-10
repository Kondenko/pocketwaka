package com.kondenko.pocketwaka.di.modules

import com.kondenko.pocketwaka.data.summary.converters.SummaryResponseConverter
import com.kondenko.pocketwaka.data.summary.converters.TimeTrackedConverter
import com.kondenko.pocketwaka.data.summary.repository.SummaryRepository
import com.kondenko.pocketwaka.data.summary.service.SummaryService
import com.kondenko.pocketwaka.di.qualifiers.Api
import com.kondenko.pocketwaka.domain.daily.usecase.GetAverage
import com.kondenko.pocketwaka.domain.daily.usecase.GetDefaultSummaryRange
import com.kondenko.pocketwaka.domain.daily.usecase.GetSummary
import com.kondenko.pocketwaka.domain.daily.usecase.GetSummaryState
import com.kondenko.pocketwaka.screens.daily.SummaryViewModel
import com.kondenko.pocketwaka.utils.extensions.create
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

val summaryModule = module {
    single {
        get<Retrofit>(Api).create<SummaryService>()
    }
    single {
        GetAverage(
                schedulersContainer = get(),
                rangeStatsRepository = get(),
                getTokenHeaderValue = get()
        )
    }
    single {
        TimeTrackedConverter(
                dateFormatter = get(),
                getAverage = get()
        )
    }
    single {
        SummaryResponseConverter()
    }
    single {
        SummaryRepository(summaryService = get())
    }
    single {
        GetSummary(
                schedulers = get(),
                summaryRepository = get(),
                commitsRepository = get(),
                durationsRepository = get(),
                getTokenHeader = get(),
                dateFormatter = get(),
                summaryResponseConverter = get<SummaryResponseConverter>(),
                timeTrackedConverter = get<TimeTrackedConverter>()
        )
    }
    single {
        GetSummaryState(
                schedulers = get(),
                getSummary = get(),
                connectivityStatusProvider = get()
        )
    }
    single {
        GetDefaultSummaryRange(
                dateProvider = get(),
                dateFormatter = get(),
                schedulers = get()
        )
    }
    viewModel {
        SummaryViewModel(
                getDefaultSummaryRange = get(),
                getSummaryState = get()
        )
    }
}