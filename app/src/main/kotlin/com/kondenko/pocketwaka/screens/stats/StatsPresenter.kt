package com.kondenko.pocketwaka.screens.stats

import com.kondenko.pocketwaka.domain.stats.GetStats
import com.kondenko.pocketwaka.screens.base.BasePresenter

class StatsPresenter(private val getStats: GetStats) : BasePresenter<StatsView>() {

    private var isLoading = false

    fun getStats(range: String) {
        val view = view // immutable view
        if (view != null && !isLoading) {
            isLoading = true
            view.showLoading()
            getStats.execute(
                    range,
                    onSuccess = { stats ->
                        if (stats.isEmpty) view.showEmptyState()
                        else view.showModel(stats)
                    },
                    onError = { view.showError(it) },
                    onFinish = { isLoading = false }
            )
        }
    }

    override fun detach() {
        dispose(getStats)
        super.detach()
    }

}