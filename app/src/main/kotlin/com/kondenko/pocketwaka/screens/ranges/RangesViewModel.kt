package com.kondenko.pocketwaka.screens.ranges

import com.kondenko.pocketwaka.domain.ranges.model.StatsUiModel
import com.kondenko.pocketwaka.domain.ranges.usecase.FetchStats
import com.kondenko.pocketwaka.domain.ranges.usecase.GetStatsState
import com.kondenko.pocketwaka.screens.BaseViewModel
import io.reactivex.Scheduler
import io.reactivex.rxkotlin.plusAssign
import java.util.concurrent.TimeUnit

class RangesViewModel(
        private val range: String?,
        private val getStats: GetStatsState,
        private val uiScheduler: Scheduler
) : BaseViewModel<List<StatsUiModel>>() {

    init {
        update()
    }

    fun update() {
        disposables += getStats
                .build(FetchStats.Params(range, refreshRate = 3))
                .debounce(50, TimeUnit.MILLISECONDS, uiScheduler)
                .subscribe(_state::postValue, this::handleError)
    }

}