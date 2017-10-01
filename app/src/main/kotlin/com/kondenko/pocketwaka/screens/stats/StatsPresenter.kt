package com.kondenko.pocketwaka.screens.stats

import android.content.Context
import com.kondenko.pocketwaka.data.stats.service.StatsService
import com.kondenko.pocketwaka.screens.BasePresenter
import com.kondenko.pocketwaka.utils.Utils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class StatsPresenter(val view: StatsView, val service: StatsService) : BasePresenter() {

    private var subscription: Disposable? = null

    fun onViewCreated(context: Context) {
//        updateData(context,, )
    }

    fun onStop() {
        Utils.unsubscribe(subscription)
    }

    fun updateData(context: Context, statsRange: String, tokenHeader: String) {
        subscription = service.getCurrentUserStats(tokenHeader, statsRange)
                .subscribeOn(Schedulers.newThread())
                .doOnSuccess { data -> data.stats.provideColors(context) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { value -> view.onSuccess(value) },
                        { error -> view.onError(error) }
                )
    }


}