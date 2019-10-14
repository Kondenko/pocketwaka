package com.kondenko.pocketwaka.di.modules

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.kondenko.pocketwaka.data.persistence.AppDatabase
import com.kondenko.pocketwaka.data.summary.converters.FetchProjects
import com.kondenko.pocketwaka.data.summary.converters.SummaryResponseConverter
import com.kondenko.pocketwaka.data.summary.converters.TimeTrackedConverter
import com.kondenko.pocketwaka.data.summary.repository.SummaryRepository
import com.kondenko.pocketwaka.data.summary.service.SummaryService
import com.kondenko.pocketwaka.di.qualifiers.Api
import com.kondenko.pocketwaka.di.qualifiers.Scheduler
import com.kondenko.pocketwaka.domain.auth.GetTokenHeaderValue
import com.kondenko.pocketwaka.domain.summary.model.SummaryUiModel
import com.kondenko.pocketwaka.domain.summary.usecase.GetAverage
import com.kondenko.pocketwaka.domain.summary.usecase.GetDefaultSummaryRange
import com.kondenko.pocketwaka.domain.summary.usecase.GetSummary
import com.kondenko.pocketwaka.domain.summary.usecase.GetSummaryState
import com.kondenko.pocketwaka.screens.summary.FragmentSummary
import com.kondenko.pocketwaka.screens.summary.SummaryAdapter
import com.kondenko.pocketwaka.screens.summary.SummaryViewModel
import com.kondenko.pocketwaka.ui.skeleton.RecyclerViewSkeleton
import com.kondenko.pocketwaka.utils.extensions.create
import com.kondenko.pocketwaka.utils.spannable.TimeSpannableCreator
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
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
        SummaryRepository(
              summaryService = get(),
              summaryDao = get(),
              workerScheduler = get(Scheduler.Worker),
              reduceModels = get<SummaryResponseConverter>()
        )
    }
    single {
        FetchProjects(
              schedulersContainer = get(),
              durationsRepository = get(),
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
              fetchProjects = get<FetchProjects>()
        )
    }
    single {
        GetDefaultSummaryRange(
              dateProvider = get(),
              schedulers = get()
        )
    }
    single {
        GetSummaryState(
              schedulers = get(),
              getSummary = get<GetSummary>(),
              connectivityStatusProvider = get()
        )
    }
    factory { (context: Context, showSkeleton: Boolean) -> SummaryAdapter(context, showSkeleton, get<TimeSpannableCreator>()) }
    scope(named<FragmentSummary>()) {
        scoped { (recyclerView: RecyclerView, context: Context, skeletonItems: List<SummaryUiModel>) ->
            RecyclerViewSkeleton(
                  recyclerView = recyclerView,
                  adapterCreator = { showSkeleton: Boolean -> get<SummaryAdapter> { parametersOf(context, showSkeleton) } },
                  skeletonItems = skeletonItems
            )
        }
    }
    viewModel {
        SummaryViewModel(
              uiScheduler = get(Scheduler.Ui),
              getDefaultSummaryRange = get(),
              getSummaryState = get<GetSummaryState>()
        )
    }
}