package com.kondenko.pocketwaka.screens.fragments.stats

import android.support.annotation.StringRes
import com.kondenko.pocketwaka.api.model.stats.Stats
import com.kondenko.pocketwaka.api.model.stats.StatsDataWrapper

interface FragmentStatsView {
    fun onSuccess(statsDataWrapper: StatsDataWrapper)
    fun onError(error: Throwable?)
    fun onRefresh()
}