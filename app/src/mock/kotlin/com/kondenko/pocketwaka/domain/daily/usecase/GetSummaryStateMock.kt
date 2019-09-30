package com.kondenko.pocketwaka.domain.daily.usecase

import com.kondenko.pocketwaka.data.android.ConnectivityStatusProvider
import com.kondenko.pocketwaka.data.summary.model.database.SummaryDbModel
import com.kondenko.pocketwaka.domain.StatefulUseCase
import com.kondenko.pocketwaka.domain.UseCaseObservable
import com.kondenko.pocketwaka.domain.daily.model.SummaryUiModel
import com.kondenko.pocketwaka.screens.State
import com.kondenko.pocketwaka.utils.SchedulersContainer
import io.reactivex.Observable
import io.reactivex.rxkotlin.zipWith
import java.util.concurrent.TimeUnit

class GetSummaryStateMock(
        schedulers: SchedulersContainer,
        useCase: UseCaseObservable<GetSummary.Params, SummaryDbModel>,
        connectivityStatusProvider: ConnectivityStatusProvider
) : StatefulUseCase<GetSummary.Params, List<SummaryUiModel>, SummaryDbModel>(schedulers, useCase, connectivityStatusProvider) {

    override fun build(params: GetSummary.Params?): Observable<State<List<SummaryUiModel>>> =
            Observable.just<State<List<SummaryUiModel>>>(
                    State.Offline(null),
                    State.Empty,
                    State.Failure.Unknown(isFatal = true)
            ).zipWith(Observable.interval(3, TimeUnit.SECONDS)) { state, _ ->
                state
            }

}