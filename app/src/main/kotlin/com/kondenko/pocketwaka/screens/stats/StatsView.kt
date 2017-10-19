package com.kondenko.pocketwaka.screens.stats

import com.kondenko.pocketwaka.data.stats.model.StatsDataWrapper
import com.kondenko.pocketwaka.screens.BaseView

interface StatsView : BaseView {
    fun onSuccess(statsDataWrapper: StatsDataWrapper)
    fun onRefresh()
}