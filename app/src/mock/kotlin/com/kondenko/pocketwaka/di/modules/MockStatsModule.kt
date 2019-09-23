package com.kondenko.pocketwaka.di.modules

import com.kondenko.pocketwaka.data.android.ConnectivityStatusProvider
import com.kondenko.pocketwaka.data.ranges.model.database.StatsDbModel
import com.kondenko.pocketwaka.data.ranges.service.RangeStatsService
import com.kondenko.pocketwaka.data.stats.service.MockStatsService
import com.kondenko.pocketwaka.domain.StatefulUseCase
import com.kondenko.pocketwaka.domain.UseCaseObservable
import com.kondenko.pocketwaka.domain.ranges.model.StatsUiModel
import com.kondenko.pocketwaka.domain.ranges.usecase.GetStatsForRanges
import com.kondenko.pocketwaka.screens.State
import com.kondenko.pocketwaka.utils.SchedulersContainer
import io.reactivex.Observable
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

class GetStatsStateMock(schedulers: SchedulersContainer, useCase: UseCaseObservable<GetStatsForRanges.Params, StatsDbModel>, connectivityStatusProvider: ConnectivityStatusProvider) : StatefulUseCase<GetStatsForRanges.Params, List<StatsUiModel>, StatsDbModel>(schedulers, useCase, connectivityStatusProvider) {

    override fun build(params: GetStatsForRanges.Params?): Observable<State<List<StatsUiModel>>> =
            Observable.just(State.Failure.Unknown<List<StatsUiModel>>(null, RuntimeException("Test"), true))

}

val mockStatsModule = module(override=true) {
    single<StatefulUseCase<GetStatsForRanges.Params, List<StatsUiModel>, StatsDbModel>> {
        GetStatsStateMock(get(), get(), get())
    }
    single { MockStatsService(androidContext(), get()) as RangeStatsService }
}