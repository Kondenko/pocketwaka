package com.kondenko.pocketwaka.data.stats.service

import android.content.Context
import com.google.gson.Gson
import com.kondenko.pocketwaka.data.stats.model.StatsDataWrapper
import com.kondenko.pocketwaka.getJsonAsset
import io.reactivex.Single
import io.reactivex.rxkotlin.toSingle
import timber.log.Timber

class MockStatsService(val context: Context, val gson: Gson) : StatsService {

    override fun getCurrentUserStats(tokenHeaderValue: String, range: String): Single<StatsDataWrapper> {
        val filename = "mocks/stats-only-time.json"
        Timber.i("Loading mock data from assets/$range")
        val stats = gson.fromJson<StatsDataWrapper>(getJsonAsset(context, filename), StatsDataWrapper::class.java)
        return stats.toSingle()
    }

}