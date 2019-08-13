package com.kondenko.pocketwaka.domain.ranges.usecase

import com.kondenko.pocketwaka.data.android.ConnectivityStatusProvider
import com.kondenko.pocketwaka.data.ranges.dto.StatsDto
import com.kondenko.pocketwaka.domain.StatefulUseCase
import com.kondenko.pocketwaka.domain.ranges.model.StatsUiModel
import com.kondenko.pocketwaka.utils.SchedulersContainer

class GetStatsState(
        schedulers: SchedulersContainer,
        fetchStats: FetchStats,
        connectivityStatusProvider: ConnectivityStatusProvider
) : StatefulUseCase<FetchStats.Params, List<StatsUiModel>, StatsDto>(schedulers, fetchStats, connectivityStatusProvider)