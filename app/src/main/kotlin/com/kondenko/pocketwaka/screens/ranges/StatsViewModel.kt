package com.kondenko.pocketwaka.screens.ranges

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kondenko.pocketwaka.domain.ranges.GetStatsState
import com.kondenko.pocketwaka.domain.ranges.model.StatsModel
import com.kondenko.pocketwaka.screens.State
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

class StatsViewModel(
        private val range: String?,
        private val getStats: GetStatsState,
        private val uiScheduler: Scheduler
) : ViewModel() {

    private var statsDisposable: Disposable? = null

    private val statsData = MutableLiveData<State<List<StatsModel>>>()

    init {
        update()
    }

    fun state(): LiveData<State<List<StatsModel>>> = statsData

    fun update() {
        statsDisposable = getStats
                .build(GetStatsState.Params(range, refreshRateMin = 3, retryAttempts = 3))
                .debounce(50, TimeUnit.MILLISECONDS, uiScheduler)
                .subscribe(statsData::postValue) {
                    statsData.postValue(State.Failure.Unknown(exception = it, isFatal = true))
                }
    }

    override fun onCleared() {
        statsDisposable?.dispose()
        super.onCleared()
    }

}