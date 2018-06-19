package com.kondenko.pocketwaka.screens.stats

import com.kondenko.pocketwaka.domain.stats.GetStats
import com.kondenko.pocketwaka.screens.base.BasePresenter
import javax.inject.Inject

class StatsPresenter @Inject constructor(private val getStats: GetStats) : BasePresenter<StatsView>() {

    private var isLoading = false

    fun getStats(range: String) {
        val view = view // immutable view
        if (view != null && !isLoading) {
            isLoading = true
            view.showLoading()
            getStats.execute(
                    range,
                    { stats ->
                        if (stats.isEmpty) view.showEmptyState()
                        else view.showModel(stats)
                    },
                    { error -> view.showError(error) },
                    {
                        isLoading = false
                    }
            )
        }
    }

    override fun detach() {
        dispose(getStats)
        super.detach()
    }

}