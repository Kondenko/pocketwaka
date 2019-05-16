package com.kondenko.pocketwaka.screens.stats

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kondenko.pocketwaka.domain.stats.GetSkeletonStats
import com.kondenko.pocketwaka.domain.stats.GetStats
import com.kondenko.pocketwaka.domain.stats.model.StatsModel
import com.kondenko.pocketwaka.screens.base.State
import com.kondenko.pocketwaka.utils.disposeAll

class StatsViewModel(private val range: String, private val getStats: GetStats, private val getSkeletonStats: GetSkeletonStats) : ViewModel() {

    private val statsData = MutableLiveData<State<StatsModel>>()

    init {
        update()
    }

    fun state(): LiveData<State<StatsModel>> = statsData

    fun update() {
        getSkeletonStats.execute(
                onSuccess = { statsData.value = State.Loading(it) },
                andThen = {
                    getStats.execute(
                            range,
                            onSuccess = { stats ->
                                if (stats.isEmpty) statsData.value = State.Empty
                                else statsData.value = State.Success(stats)
                            },
                            onError = {
                                statsData.value = State.Failure(it)
                            }
                    )
                }
        )
    }


    override fun onCleared() {
        super.onCleared()
        disposeAll(getStats, getSkeletonStats)
    }


}