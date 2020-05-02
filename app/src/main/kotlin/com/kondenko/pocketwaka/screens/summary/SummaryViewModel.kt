package com.kondenko.pocketwaka.screens.summary

import com.kondenko.pocketwaka.domain.UseCase
import com.kondenko.pocketwaka.domain.summary.model.SummaryUiModel
import com.kondenko.pocketwaka.domain.summary.usecase.GetSummary
import com.kondenko.pocketwaka.screens.State
import com.kondenko.pocketwaka.screens.base.BaseViewModel
import com.kondenko.pocketwaka.utils.extensions.debounceStateUpdates
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.rxkotlin.plusAssign

class SummaryViewModel(
      private val range: SummaryDate,
      private val uiScheduler: Scheduler,
      private val getSummaryState: UseCase<GetSummary.Params, State<List<SummaryUiModel>>, Observable<State<List<SummaryUiModel>>>>
) : BaseViewModel<List<SummaryUiModel>>() {

    private val refreshRate = 1

    private val retryAttempts = 1

    init {
        fetchSummary()
    }

    fun fetchSummary() {
        disposables += getSummaryState.build(GetSummary.Params(
              range.toDateRange(),
              refreshRate = refreshRate,
              retryAttempts = retryAttempts
        ))
              .debounceStateUpdates(timeout = 100, scheduler = uiScheduler)
              .subscribe(::setState, this::handleError)
    }

    fun connectRepoClicked(url: String) {
        setState(SummaryState.ConnectRepo(url, state?.data))
    }

    fun updateDataIfRepoHasBeenConnected() {
        if (state is SummaryState.ConnectRepo) {
            fetchSummary()
        }
    }

}
