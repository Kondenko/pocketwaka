package com.kondenko.pocketwaka.screens.stats

import com.kondenko.pocketwaka.dagger.PerApp
import com.kondenko.pocketwaka.domain.stats.GetStats
import com.kondenko.pocketwaka.screens.base.BasePresenter
import javax.inject.Inject

@PerApp
class StatsPresenter @Inject constructor(private val getStats: GetStats) : BasePresenter<StatsView>() {

    fun getStats(range: String) {
        view?.showLoading()
        getStats.execute(
            range,
            { stats -> view?.let {
                if (stats.humanReadableDailyAverage == null || stats.humanReadableTotal == null) it.showEmptyState()
                else it.showModel(stats)
            } },
            { error -> view?.showError(error) }
        )
    }

    override fun detach() {
        super.detach()
        dispose(getStats)
    }

}