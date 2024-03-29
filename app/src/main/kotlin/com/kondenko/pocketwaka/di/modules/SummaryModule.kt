package com.kondenko.pocketwaka.di.modules

import android.content.Context
import com.kondenko.pocketwaka.data.android.HumanReadableDateFormatter
import com.kondenko.pocketwaka.data.persistence.AppDatabase
import com.kondenko.pocketwaka.data.summary.converters.SummaryResponseConverter
import com.kondenko.pocketwaka.data.summary.converters.TimeTrackedConverter
import com.kondenko.pocketwaka.data.summary.repository.SummaryRepository
import com.kondenko.pocketwaka.data.summary.service.SummaryService
import com.kondenko.pocketwaka.di.qualifiers.Api
import com.kondenko.pocketwaka.di.qualifiers.Scheduler
import com.kondenko.pocketwaka.domain.auth.GetTokenHeaderValue
import com.kondenko.pocketwaka.domain.main.ClearCache
import com.kondenko.pocketwaka.domain.summary.model.SummaryUiModel
import com.kondenko.pocketwaka.domain.summary.usecase.*
import com.kondenko.pocketwaka.screens.summary.DatePickerViewModel
import com.kondenko.pocketwaka.screens.summary.FragmentSummary
import com.kondenko.pocketwaka.screens.summary.SummaryAdapter
import com.kondenko.pocketwaka.screens.summary.SummaryViewModel
import com.kondenko.pocketwaka.ui.skeleton.RecyclerViewSkeleton
import com.kondenko.pocketwaka.utils.date.DateRange
import com.kondenko.pocketwaka.utils.extensions.create
import com.kondenko.pocketwaka.utils.spannable.TimeSpannableCreator
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import retrofit2.Retrofit

val summaryModule = module {
    single {
        get<Retrofit>(Api).create<SummaryService>()
    }
    single {
        get<AppDatabase>().summaryDao()
    }
    single {
        GetAverage(
            schedulersContainer = get(),
            statsRepository = get(),
            getTokenHeaderValue = get<GetTokenHeaderValue>()
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
        SummaryRepository(
            summaryService = get(),
            summaryDao = get(),
            workerScheduler = get(Scheduler.Worker),
            reduceModels = get<SummaryResponseConverter>()
        )
    }
    single {
        FetchBranchesAndCommits(
            schedulersContainer = get(),
            summaryService = get(),
            commitsRepository = get(),
            dateFormatter = get()
        )
    }
    single {
        GetSummary(
            schedulers = get(),
            summaryRepository = get<SummaryRepository>(),
            getTokenHeader = get<GetTokenHeaderValue>(),
            dateFormatter = get(),
            summaryResponseConverter = get<SummaryResponseConverter>(),
            timeTrackedConverter = get<TimeTrackedConverter>(),
            fetchBranchesAndCommits = get<FetchBranchesAndCommits>()
        )
    }
    single {
        ShouldShowOnboarding(get())
    }
    single {
        SetOnboardingShown(get())
    }
    single {
        GetSummaryState(
            getSummary = get<GetSummary>(),
            clearCache = get<ClearCache>(),
            shouldShowOnboarding = get<ShouldShowOnboarding>(),
            connectivityStatusProvider = get(),
            schedulers = get()
        )
    }
    factory { (context: Context, showSkeleton: Boolean) ->
        SummaryAdapter(
            context,
            showSkeleton,
            get<TimeSpannableCreator>(),
            get()
        )
    }
    factory {
        GetAvailableRange(get(), get(), get())
    }
    factory {
        HumanReadableDateFormatter(get(), get(), get())
    }
    viewModel {
        DatePickerViewModel(get(), get(), get(), get())
    }
    viewModel { (date: DateRange) ->
        SummaryViewModel(
            range = date,
            uiScheduler = get(Scheduler.Ui),
            getSummaryState = get<GetSummaryState>(),
            setOnboardingShown = get<SetOnboardingShown>()
        )
    }
    scope<FragmentSummary> {
        scoped { (context: Context, skeletonItems: List<SummaryUiModel>) ->
            RecyclerViewSkeleton(
                adapterCreator = { showSkeleton: Boolean ->
                    get<SummaryAdapter> { parametersOf(context, showSkeleton) }
                },
                skeletonItems = skeletonItems
            )
        }
    }
}