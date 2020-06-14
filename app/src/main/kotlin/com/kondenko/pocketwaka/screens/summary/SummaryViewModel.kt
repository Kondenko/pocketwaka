package com.kondenko.pocketwaka.screens.summary

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kondenko.pocketwaka.domain.UseCase
import com.kondenko.pocketwaka.domain.summary.model.SummaryUiModel
import com.kondenko.pocketwaka.domain.summary.usecase.GetSummary
import com.kondenko.pocketwaka.screens.State
import com.kondenko.pocketwaka.screens.base.BaseViewModel
import com.kondenko.pocketwaka.utils.date.DateRange
import com.kondenko.pocketwaka.utils.extensions.debounceStateUpdates
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.rxkotlin.plusAssign

class SummaryViewModel(
      private val range: DateRange,
      private val uiScheduler: Scheduler,
      private val getSummaryState: UseCase<GetSummary.Params, State<List<SummaryUiModel>>, Observable<State<List<SummaryUiModel>>>>,
      private val hasPremiumFeatures: UseCase<Nothing?, Boolean, Single<Boolean>>
) : BaseViewModel<List<SummaryUiModel>>() {

    private val refreshRate = 1

    private val retryAttempts = 1

    private val premiumFeaturesAvailable = MutableLiveData<Boolean>()

    init {
        fetchSummary()
        hasPremiumFeatures(
              null,
              onSuccess = premiumFeaturesAvailable::postValue,
              onError = this::handleError
        )
    }

    fun premiumFeaturesAvailable(): LiveData<Boolean> = premiumFeaturesAvailable

    fun fetchSummary() {
        disposables += getSummaryState.build(GetSummary.Params(
              range,
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

    override fun onCleared() {
        hasPremiumFeatures.dispose()
        super.onCleared()
    }
}
