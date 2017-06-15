package com.kondenko.pocketwaka.screens.fragments.stats

import android.content.Context
import com.kondenko.pocketwaka.App
import com.kondenko.pocketwaka.BasePresenter
import com.kondenko.pocketwaka.api.services.StatsService
import com.kondenko.pocketwaka.utils.Utils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class FragmentStatsPresenter(val statsRange: String, val tokenHeaderValue: String, val view: FragmentStatsView) : BasePresenter() {

    @Inject
    lateinit var service: StatsService

    private var subscription: Disposable? = null

    init {
        App.serviceComponent.inject(this)
    }

    fun onViewCreated(context: Context) {
        updateData(context)
    }

    fun onStop() {
        Utils.unsubscribe(subscription)
    }

    fun updateData(context: Context) {
        subscription = service.getCurrentUserStats(tokenHeaderValue, statsRange)
                .subscribeOn(Schedulers.newThread())
                .doOnSuccess { data -> data.stats.provideColors(context) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { value -> view.onSuccess(value) },
                        { error -> view.onError(error) }
                )
    }


}