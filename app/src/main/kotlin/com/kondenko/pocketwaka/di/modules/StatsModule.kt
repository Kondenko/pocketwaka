package com.kondenko.pocketwaka.di.modules

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.kondenko.pocketwaka.data.android.ColorProvider
import com.kondenko.pocketwaka.data.persistence.AppDatabase
import com.kondenko.pocketwaka.data.ranges.converter.RangeResponseConverter
import com.kondenko.pocketwaka.data.ranges.repository.RangeStatsRepository
import com.kondenko.pocketwaka.data.ranges.service.RangeStatsService
import com.kondenko.pocketwaka.di.qualifiers.Api
import com.kondenko.pocketwaka.domain.auth.GetTokenHeaderValue
import com.kondenko.pocketwaka.domain.ranges.model.StatsUiModel
import com.kondenko.pocketwaka.domain.ranges.usecase.GetStatsForRanges
import com.kondenko.pocketwaka.domain.ranges.usecase.GetStatsState
import com.kondenko.pocketwaka.screens.ranges.FragmentStatsTab
import com.kondenko.pocketwaka.screens.ranges.RangesViewModel
import com.kondenko.pocketwaka.screens.ranges.adapter.StatsAdapter
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

val rangeStatsModule = module {
    factory { get<Retrofit>(Api).create<RangeStatsService>() }
    factory { get<AppDatabase>().statsDao() }
    factory {
        RangeStatsRepository(
                service = get(),
                dao = get()
        )
    }
    factory { ColorProvider(androidContext()) }
    factory {
        RangeResponseConverter(
                context = androidContext(),
                colorProvider = get(),
                dateProvider = get(),
                dateFormatter = get()
        )
    }
    factory {
        GetStatsForRanges(
                schedulers = get(),
                getTokenHeader = get() as GetTokenHeaderValue,
                rangeStatsRepository = get(),
                serverModelConverter = get<RangeResponseConverter>()
        )
    }
    factory {
        GetStatsState(
                schedulers = get(),
                getStatsForRanges = get(),
                connectivityStatusProvider = get()
        )
    }
    factory { (context: Context, showSkeleton: Boolean) -> StatsAdapter(context, showSkeleton, get<TimeSpannableCreator>()) }
    scope(named<FragmentStatsTab>()) {
        scoped { (recyclerView: RecyclerView, skeletonItems: List<StatsUiModel>) ->
            RecyclerViewSkeleton(
                    adapterCreator = { showSkeleton: Boolean -> get<StatsAdapter> { parametersOf(recyclerView.context, showSkeleton) } },
                    skeletonItems = skeletonItems
            )
        }
    }
    viewModel { (range: String?) ->
        RangesViewModel(
                range,
                getStats = get(),
                uiScheduler = get<SchedulersContainer>().uiScheduler
        )
    }

}

