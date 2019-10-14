package com.kondenko.pocketwaka.screens.summary

import com.kondenko.pocketwaka.data.summary.model.database.SummaryDbModel
import com.kondenko.pocketwaka.domain.StatefulUseCase
import com.kondenko.pocketwaka.domain.summary.model.SummaryUiModel
import com.kondenko.pocketwaka.domain.summary.usecase.GetDefaultSummaryRange
import com.kondenko.pocketwaka.domain.summary.usecase.GetSummary
import com.kondenko.pocketwaka.screens.base.BaseViewModel
import io.reactivex.rxkotlin.plusAssign

class SummaryViewModel(
        private val getDefaultSummaryRange: GetDefaultSummaryRange,
        private val getSummaryState: StatefulUseCase<GetSummary.Params, List<SummaryUiModel>, SummaryDbModel>
) : BaseViewModel<List<SummaryUiModel>>() {

    private val refreshRate = 1

    private val retryAttempts = 1

    init {
        getSummaryForRange() // For today
    }

    fun getSummaryForRange() {
        val rangeSource = getDefaultSummaryRange()
        disposables += rangeSource.flatMapObservable { range ->
            getSummaryState.build(GetSummary.Params(range, refreshRate = refreshRate, retryAttempts = retryAttempts))
        }.subscribe(::setState, this::handleError)
    }

    fun connectRepoClicked(url: String) {
        setState(SummaryState.ConnectRepo(url, state?.data))
    }

    fun onResume() {
        if (state is SummaryState.ConnectRepo) {
            getSummaryForRange()
        }
    }

}
