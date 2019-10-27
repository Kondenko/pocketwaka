package com.kondenko.pocketwaka.domain.stats.usecase

import com.kondenko.pocketwaka.domain.UseCase
import com.kondenko.pocketwaka.domain.stats.model.StatsUiModel
import com.kondenko.pocketwaka.screens.State
import io.reactivex.Observable

class GetStatsStateMock(private val getStatsState: GetStatsState)
    : UseCase<GetStatsForRange.Params, State<List<StatsUiModel>>, Observable<State<List<StatsUiModel>>>> by getStatsState {

    override fun build(params: GetStatsForRange.Params?): Observable<State<List<StatsUiModel>>> =
          Observable.just(State.Empty)

}