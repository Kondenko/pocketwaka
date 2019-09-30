package com.kondenko.pocketwaka.di.modules

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.kondenko.pocketwaka.data.android.ColorProvider
import com.kondenko.pocketwaka.data.persistence.AppDatabase
import com.kondenko.pocketwaka.data.ranges.converter.RangeResponseConverter
import com.kondenko.pocketwaka.data.ranges.model.database.StatsDbModel
import com.kondenko.pocketwaka.data.ranges.model.server.StatsServerModel
import com.kondenko.pocketwaka.data.ranges.repository.RangeStatsRepository
import com.kondenko.pocketwaka.data.ranges.service.RangeStatsService
import com.kondenko.pocketwaka.di.qualifiers.Api
import com.kondenko.pocketwaka.domain.UseCaseObservable
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
import io.reactivex.Maybe
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
    factory<(RangeStatsRepository.Params, StatsServerModel) -> Maybe<StatsDbModel>?> {
        RangeResponseConverter(
                context = androidContext(),
                colorProvider = get(),
                dateProvider = get(),
                dateFormatter = get()
        )
    }
    factory<UseCaseObservable<GetStatsForRanges.Params, StatsDbModel>> {
        GetStatsForRanges(
                schedulers = get(),
                getTokenHeader = get() as GetTokenHeaderValue,
                rangeStatsRepository = get(),
                serverModelConverter = get()
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
        scoped { (recyclerView: RecyclerView, context: Context, skeletonItems: List<StatsUiModel>) ->
            RecyclerViewSkeleton<StatsUiModel, StatsAdapter>(
                    recyclerView = recyclerView,
                    adapterCreator = { showSkeleton: Boolean -> get { parametersOf(context, showSkeleton) } },
                    skeletonItems = skeletonItems
            )
        }
    }
    viewModel { (range: String?) ->
        RangesViewModel(
                range = range,
                getStats = get<GetStatsState>(),
                uiScheduler = get<SchedulersContainer>().uiScheduler
        )
    }

}

