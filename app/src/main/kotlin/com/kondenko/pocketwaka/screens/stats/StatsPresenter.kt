package com.kondenko.pocketwaka.screens.stats

import com.kondenko.pocketwaka.dagger.PerApp
import com.kondenko.pocketwaka.domain.stats.GetStats
import com.kondenko.pocketwaka.screens.BasePresenter
import javax.inject.Inject

@PerApp
class StatsPresenter
@Inject constructor(private val getStats: GetStats) : BasePresenter<StatsView>() {

    fun getStats(range: String) {
        getStats.execute(
            range,
            { stats -> view?.onSuccess(stats) },
            { error -> view?.onError(error) }
        )
    }

    override fun detach() {
        super.detach()
        dispose(getStats)
    }

}