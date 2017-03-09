package com.kondenko.pocketwaka.screens.fragments.stats

import android.content.Context
import com.kondenko.pocketwaka.App
import com.kondenko.pocketwaka.Const
import com.kondenko.pocketwaka.api.services.StatsService
import retrofit2.Retrofit
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Named

class FragmentStatsPresenter(val statsRange: String, val tokenHeaderValue: String, val view: FragmentStatsView) {

    @Inject
    @field:[Named(Const.URL_TYPE_API)]
    lateinit var retrofit: Retrofit

    private val service: StatsService
    private var statsSubscription: Subscription? = null

    init {
        App.netComponent.inject(this)
        service = retrofit.create(StatsService::class.java)
    }

    fun onViewCreated(context: Context) {
        updateData(context)
    }

    fun onStop() {
        statsSubscription?.let {
            if (!it.isUnsubscribed) it.unsubscribe()
        }
    }

    fun updateData(context: Context) {
        statsSubscription = service.getCurrentUserStats(tokenHeaderValue, statsRange)
                .subscribeOn(Schedulers.newThread())
                .doOnSuccess { data -> data.stats.provideColors(context) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { value -> view.onSuccess(value) },
                        { error -> view.onError(error) }
                )
    }


}

