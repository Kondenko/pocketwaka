package com.kondenko.pocketwaka.screens.stats

import com.kondenko.pocketwaka.dagger.PerApp
import com.kondenko.pocketwaka.domain.stats.GetStats
import com.kondenko.pocketwaka.screens.base.BasePresenter
import javax.inject.Inject

@PerApp
 class StatsPresenter
@Inject constructor(private val getStats: GetStats) : BasePresenter<StatsView>() {

    fun getStats(range: String) {
        view?.setLoading(true)
        getStats.execute(
            range,
            { stats -> view?.let { it.onSuccess(stats); it.setLoading(false)} },
            { error -> view?.let { it.onError(error); it.setLoading(false)} }
        )
    }

    override fun detach() {
        super.detach()
        dispose(getStats)
    }

}