package com.kondenko.pocketwaka.domain.stats.usecase

import com.kondenko.pocketwaka.data.android.ConnectivityStatusProvider
import com.kondenko.pocketwaka.data.stats.model.database.StatsDbModel
import com.kondenko.pocketwaka.domain.StatefulUseCase
import com.kondenko.pocketwaka.domain.UseCaseObservable
import com.kondenko.pocketwaka.domain.stats.model.StatsUiModel
import com.kondenko.pocketwaka.utils.SchedulersContainer

class GetStatsStateMock(
        schedulers: SchedulersContainer,
        useCase: UseCaseObservable<GetStatsForRange.Params, StatsDbModel>,
        connectivityStatusProvider: ConnectivityStatusProvider
) : StatefulUseCase<GetStatsForRange.Params, List<StatsUiModel>, StatsDbModel>(
        schedulers,
        useCase,
        connectivityStatusProvider
)