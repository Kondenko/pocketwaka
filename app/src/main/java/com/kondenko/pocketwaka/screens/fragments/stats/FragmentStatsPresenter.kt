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

    fun onResume() {
        getStats()
    }

    fun getStats() {
        view.setLoading(true)
        service.getCurrentUserStats(tokenHeaderValue, statsRange)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { data -> setColors(data) }
                .cache()
                .subscribe(
                        { data -> view.onSuccess(data) },
                        { error -> view.onError(error, R.string.error_loading_stats) }
                )
    }

    private fun setColors(data: DataWrapper): DataWrapper {
        val dataArrays = arrayOf(data.stats.editors, data.stats.languages, data.stats.projects, data.stats.operatingSystems)
        for (array in dataArrays) {
            array?.let {
                val colors = ColorGenerator.getColors(array.size)
                for (i in 0..(array.size - 1)) {
                    array[i].color = colors[i]
                }
            }
        }
        return data
    }

}

