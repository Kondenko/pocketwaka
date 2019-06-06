package com.kondenko.pocketwaka.screens.stats

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kondenko.pocketwaka.domain.stats.GetStatsState
import com.kondenko.pocketwaka.domain.stats.model.StatsModel
import com.kondenko.pocketwaka.screens.base.State

class StatsViewModel(private val range: String?, private val getStats: GetStatsState) : ViewModel() {

    private val refreshRateMin = 3

    private val statsData = MutableLiveData<State<List<StatsModel>>>()

    init {
        update()
    }

    fun state(): LiveData<State<List<StatsModel>>> = statsData

    fun update() {
        getStats.execute(GetStatsState.Params(range, refreshRateMin), onSuccess = statsData::setValue)
    }

    override fun onCleared() {
        super.onCleared()
        getStats.dispose()
    }

}