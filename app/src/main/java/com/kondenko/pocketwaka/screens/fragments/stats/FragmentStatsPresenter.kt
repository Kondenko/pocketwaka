package com.kondenko.pocketwaka.screens.fragments.stats

import com.kondenko.pocketwaka.App
import com.kondenko.pocketwaka.api.model.stats.StatsDataWrapper
import com.kondenko.pocketwaka.api.services.StatsService
import com.kondenko.pocketwaka.utils.SingleSubscriberLambda
import retrofit2.Retrofit
import rx.Single
import rx.android.schedulers.AndroidSchedulers
import rx.lang.kotlin.subscriber
import rx.schedulers.Schedulers
import javax.inject.Inject

class FragmentStatsPresenter(val statsRange: String, val tokenHeaderValue: String, val view: FragmentStatsView) {

    private val TAG = this.javaClass.simpleName + "@" + statsRange

    @Inject lateinit var retrofit: Retrofit

    private val service: StatsService
    private val statsSubscriber: SingleSubscriberLambda<StatsDataWrapper>
    private var statsSingle: Single<StatsDataWrapper>? = null

    init {
        App.apiComponent.inject(this)
        service = retrofit.create(StatsService::class.java)
        statsSubscriber = SingleSubscriberLambda<StatsDataWrapper>(
                { value -> view.onSuccess(value) },
                { error -> view.onError(error) }
        )
    }

    fun onCreate() {
        statsSingle = getStatsForSubscription()
    }

    fun onStart() {
        updateData()
    }

    fun onStop() {
        if (!statsSubscriber.isUnsubscribed) {
            statsSubscriber.unsubscribe()
        }
    }

    fun updateData() {
        statsSingle?.subscribe(statsSubscriber)
    }

    private fun getStatsForSubscription(): Single<StatsDataWrapper> {
        return service.getCurrentUserStats(tokenHeaderValue, statsRange)
                .subscribeOn(Schedulers.newThread())
                .doOnSuccess { data -> data.stats.provideColors() }
                .observeOn(AndroidSchedulers.mainThread())
    }

}

