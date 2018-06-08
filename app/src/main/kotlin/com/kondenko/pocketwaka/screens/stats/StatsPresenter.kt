package com.kondenko.pocketwaka.screens.stats

import com.kondenko.pocketwaka.domain.stats.GetStats
import com.kondenko.pocketwaka.screens.base.BasePresenter
import timber.log.Timber
import javax.inject.Inject

class StatsPresenter @Inject constructor(private val getStats: GetStats) : BasePresenter<StatsView>() {

    fun getStats(range: String) {
        view?.let {
            Timber.d("Loading stats for $range")
            it.showLoading()
            getStats.execute(
                    range,
                    { stats ->
                        if (stats.isEmpty) it.showEmptyState()
                        else it.showModel(stats)
                    },
                    { error -> it.showError(error) }
            )
        }
    }

    override fun detach() {
        dispose(getStats)
        super.detach()
    }

}