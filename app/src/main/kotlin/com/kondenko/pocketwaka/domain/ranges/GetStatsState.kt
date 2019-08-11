package com.kondenko.pocketwaka.domain.ranges

import com.kondenko.pocketwaka.data.android.ConnectivityStatusProvider
import com.kondenko.pocketwaka.data.ranges.dto.StatsDto
import com.kondenko.pocketwaka.domain.StatefulUseCase
import com.kondenko.pocketwaka.domain.ranges.model.StatsModel
import com.kondenko.pocketwaka.utils.SchedulersContainer

class GetStatsState(
        schedulers: SchedulersContainer,
        fetchStats: FetchStats,
        connectivityStatusProvider: ConnectivityStatusProvider
) : StatefulUseCase<String, List<StatsModel>, StatsDto>(schedulers, fetchStats, connectivityStatusProvider) {

    class Params(val range: String?, refreshRateMin: Int, retryAttempts: Int) : StatefulUseCase.ParamsWrapper<String?>(range, refreshRateMin, retryAttempts) {
        override fun isValid(): Boolean = range != null
    }

}