package com.kondenko.pocketwaka.domain.stats

import com.kondenko.pocketwaka.data.stats.repository.StatsRepository
import com.kondenko.pocketwaka.domain.UseCaseObservable
import com.kondenko.pocketwaka.domain.auth.GetTokenHeaderValue
import com.kondenko.pocketwaka.domain.stats.model.StatsModel
import com.kondenko.pocketwaka.utils.SchedulersContainer
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable


class FetchStats(
        schedulers: SchedulersContainer,
        private val disposables: CompositeDisposable,
        private val getTokenHeader: GetTokenHeaderValue,
        private val statsRepository: StatsRepository
) : UseCaseObservable<String, List<StatsModel>>(schedulers) {

    override fun build(range: String?): Observable<List<StatsModel>> {
        return getTokenHeader.build()
                .flatMapObservable { header ->
                    statsRepository.getStats(header, range!!)
                }
                .map { it.stats }
    }

    override fun dispose() {
        super.dispose()
        disposables.dispose()
    }

    override fun isDisposed() = super.isDisposed() && disposables.isDisposed

}