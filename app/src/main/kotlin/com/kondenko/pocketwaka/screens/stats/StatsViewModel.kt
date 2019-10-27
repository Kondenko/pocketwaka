package com.kondenko.pocketwaka.screens.stats

import com.kondenko.pocketwaka.domain.UseCase
import com.kondenko.pocketwaka.domain.stats.model.StatsUiModel
import com.kondenko.pocketwaka.domain.stats.usecase.GetStatsForRange
import com.kondenko.pocketwaka.screens.State
import com.kondenko.pocketwaka.screens.base.BaseViewModel
import com.kondenko.pocketwaka.utils.extensions.debounceStateUpdates
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.rxkotlin.plusAssign

class StatsViewModel(
      private val range: String?,
      private val getStats: UseCase<GetStatsForRange.Params, State<List<StatsUiModel>>, Observable<State<List<StatsUiModel>>>>,
      private val uiScheduler: Scheduler
) : BaseViewModel<List<StatsUiModel>>() {

    init {
        update()
    }

    fun update() {
        disposables += getStats
                .build(GetStatsForRange.Params(range, refreshRate = 3))
                .debounceStateUpdates(scheduler = uiScheduler)
                .subscribe(::setState, this::handleError)
    }

}