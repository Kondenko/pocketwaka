package com.kondenko.pocketwaka.domain.ranges.usecase

import com.kondenko.pocketwaka.data.android.ConnectivityStatusProvider
import com.kondenko.pocketwaka.data.ranges.model.database.StatsDbModel
import com.kondenko.pocketwaka.domain.StatefulUseCase
import com.kondenko.pocketwaka.domain.UseCaseObservable
import com.kondenko.pocketwaka.domain.ranges.model.StatsUiModel
import com.kondenko.pocketwaka.utils.SchedulersContainer

class GetStatsState(
        schedulers: SchedulersContainer,
        getStatsForRanges: UseCaseObservable<GetStatsForRanges.Params, StatsDbModel>,
        connectivityStatusProvider: ConnectivityStatusProvider
) : StatefulUseCase<GetStatsForRanges.Params, List<StatsUiModel>, StatsDbModel>(
        schedulers,
        getStatsForRanges,
        connectivityStatusProvider
)