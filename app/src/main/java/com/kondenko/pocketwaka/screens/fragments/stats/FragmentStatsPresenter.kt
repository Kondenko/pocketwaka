package com.kondenko.pocketwaka.screens.fragments.stats

import com.kondenko.pocketwaka.App
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.api.model.stats.DataWrapper
import com.kondenko.pocketwaka.api.services.StatsService
import com.kondenko.pocketwaka.ui.ColorGenerator
import retrofit2.Retrofit
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

class FragmentStatsPresenter(val statsRange: String, val tokenHeaderValue: String, val view: FragmentStatsView) {

    @Inject
    lateinit var retrofit: Retrofit
    private var service: StatsService

    init {
        App.apiComponent.inject(this)
        service = retrofit.create(StatsService::class.java)
    }

    fun onStart() {
        getStats()
    }

    fun getStats() {
        view.onRefresh()
        service.getCurrentUserStats(tokenHeaderValue, statsRange)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess { data -> data.stats.provideColors() }
                .subscribe(
                        { data -> view.onSuccess(data) },
                        { error -> view.onError(error) }
                )
    }

}

