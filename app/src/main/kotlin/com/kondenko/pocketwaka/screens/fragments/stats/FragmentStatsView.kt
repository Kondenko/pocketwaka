package com.kondenko.pocketwaka.screens.fragments.stats

import com.kondenko.pocketwaka.BaseView
import com.kondenko.pocketwaka.api.model.stats.StatsDataWrapper

interface FragmentStatsView : BaseView {
    fun onSuccess(statsDataWrapper: StatsDataWrapper)
    fun onError(error: Throwable?)
    fun onRefresh()
}