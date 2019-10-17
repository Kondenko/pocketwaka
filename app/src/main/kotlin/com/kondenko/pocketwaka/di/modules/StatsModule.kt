package com.kondenko.pocketwaka.di.modules

import android.content.Context
import com.kondenko.pocketwaka.data.android.ColorProvider
import com.kondenko.pocketwaka.data.persistence.AppDatabase
import com.kondenko.pocketwaka.data.stats.converter.StatsResponseConverter
import com.kondenko.pocketwaka.data.stats.repository.StatsRepository
import com.kondenko.pocketwaka.data.stats.service.RangeStatsService
import com.kondenko.pocketwaka.di.qualifiers.Api
import com.kondenko.pocketwaka.domain.auth.GetTokenHeaderValue
import com.kondenko.pocketwaka.domain.stats.model.StatsUiModel
import com.kondenko.pocketwaka.domain.stats.usecase.GetStatsForRange
import com.kondenko.pocketwaka.domain.stats.usecase.GetStatsState
import com.kondenko.pocketwaka.screens.stats.FragmentStatsTab
import com.kondenko.pocketwaka.screens.stats.StatsViewModel
import com.kondenko.pocketwaka.screens.stats.adapter.StatsAdapter
import com.kondenko.pocketwaka.ui.skeleton.RecyclerViewSkeleton
import com.kondenko.pocketwaka.utils.SchedulersContainer
import com.kondenko.pocketwaka.utils.extensions.create
import com.kondenko.pocketwaka.utils.spannable.TimeSpannableCreator
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit

val statsModule = module {
    factory { get<Retrofit>(Api).create<RangeStatsService>() }
    factory { get<AppDatabase>().statsDao() }
    factory {
        StatsRepository(
                service = get(),
                dao = get()
        )
    }
    factory { ColorProvider(androidContext()) }
    factory {
        StatsResponseConverter(
                context = androidContext(),
                colorProvider = get(),
                dateProvider = get(),
                dateFormatter = get()
        )
    }
    factory {
        GetStatsForRange(
                schedulers = get(),
                getTokenHeader = get<GetTokenHeaderValue>(),
                statsRepository = get(),
                serverModelConverter = get<StatsResponseConverter>()
        )
    }
    factory {
        GetStatsState(
                schedulers = get(),
                getStatsForRange = get<GetStatsForRange>(),
                connectivityStatusProvider = get()
        )
    }
    factory { (context: Context, showSkeleton: Boolean) -> StatsAdapter(context, showSkeleton, get<TimeSpannableCreator>()) }
    scope(named<FragmentStatsTab>()) {
        scoped { (context: Context, skeletonItems: List<StatsUiModel>) ->
            RecyclerViewSkeleton<StatsUiModel, StatsAdapter>(
                    adapterCreator = { showSkeleton: Boolean -> get { parametersOf(context, showSkeleton) } },
                    skeletonItems = skeletonItems
            )
        }
    }
    viewModel { (range: String?) ->
        StatsViewModel(
                range = range,
                getStats = get<GetStatsState>(),
                uiScheduler = get<SchedulersContainer>().uiScheduler
        )
    }

}

