package com.kondenko.pocketwaka.screens.stats

import android.content.Context
import com.kondenko.pocketwaka.data.stats.service.StatsService
import com.kondenko.pocketwaka.screens.BasePresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class StatsPresenter(val service: StatsService) : BasePresenter<StatsView>() {

    private var disposable: Disposable? = null

    fun onViewCreated() {
//        updateData(context,, )
    }

    fun onStop() {
        disposable?.dispose()
    }

    fun updateData(context: Context, statsRange: String, tokenHeader: String) {
        disposable = service.getCurrentUserStats(tokenHeader, statsRange)
                .subscribeOn(Schedulers.newThread())
                .doOnSuccess { data -> data.stats.provideColors(context) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { value -> view?.onSuccess(value) },
                        { error -> view?.onError(error) }
                )
    }


}