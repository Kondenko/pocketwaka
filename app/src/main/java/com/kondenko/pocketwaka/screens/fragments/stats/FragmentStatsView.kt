package com.kondenko.pocketwaka.screens.fragments.stats

import android.support.annotation.StringRes
import com.kondenko.pocketwaka.api.model.stats.Stats
import com.kondenko.pocketwaka.api.model.stats.DataWrapper

interface FragmentStatsView {
    fun onSuccess(dataWrapper: DataWrapper)
    fun onError(error: Throwable?, @StringRes messageString: Int)
}