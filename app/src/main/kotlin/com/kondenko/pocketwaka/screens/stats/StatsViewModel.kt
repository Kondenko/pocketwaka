package com.kondenko.pocketwaka.screens.stats

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kondenko.pocketwaka.domain.stats.GetSkeletonStats
import com.kondenko.pocketwaka.domain.stats.GetStats
import com.kondenko.pocketwaka.domain.stats.model.StatsModel
import com.kondenko.pocketwaka.screens.base.State
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

class StatsViewModel(private val range: String, private val getStats: GetStats, private val getSkeletonStats: GetSkeletonStats) : ViewModel() {

    private val disposables = CompositeDisposable()

    private val statsData = MutableLiveData<State<StatsModel>>()

    init {
        update()
    }

    fun state(): LiveData<State<StatsModel>> = statsData

    fun update() {
        disposables += getSkeletonStats
                .build()
                .doOnSuccess { statsData.postValue(State.Loading(it)) }
                .flatMap { getStats.build(range) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onSuccess = { stats ->
                            if (stats.isEmpty) statsData.value = State.Empty
                            else statsData.value = State.Success(stats)
                        },
                        onError = {
                            statsData.value = State.Failure(it)
                        }
                )
    }


    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }


}