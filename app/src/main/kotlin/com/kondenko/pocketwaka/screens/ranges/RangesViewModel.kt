package com.kondenko.pocketwaka.screens.ranges

import com.kondenko.pocketwaka.data.ranges.model.database.StatsDbModel
import com.kondenko.pocketwaka.domain.StatefulUseCase
import com.kondenko.pocketwaka.domain.ranges.model.StatsUiModel
import com.kondenko.pocketwaka.domain.ranges.usecase.GetStatsForRanges
import com.kondenko.pocketwaka.screens.base.BaseViewModel
import io.reactivex.Scheduler
import io.reactivex.rxkotlin.plusAssign
import java.util.concurrent.TimeUnit

class RangesViewModel(
        private val range: String?,
        private val getStats: StatefulUseCase<GetStatsForRanges.Params, List<StatsUiModel>, StatsDbModel>,
        private val uiScheduler: Scheduler
) : BaseViewModel<List<StatsUiModel>>() {

    init {
        update()
    }

    fun update() {
        disposables += getStats
                .build(GetStatsForRanges.Params(range, refreshRate = 3))
                .debounce(50, TimeUnit.MILLISECONDS, uiScheduler)
                .subscribe(_state::postValue, this::handleError)
    }

}