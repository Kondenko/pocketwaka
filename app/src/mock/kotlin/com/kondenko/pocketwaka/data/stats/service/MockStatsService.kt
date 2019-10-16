package com.kondenko.pocketwaka.data.stats.service

import android.content.Context
import com.google.gson.Gson
import com.kondenko.pocketwaka.data.stats.model.server.StatsServerModel
import com.kondenko.pocketwaka.jsonToServiceModel
import io.reactivex.Single

class MockStatsService(val context: Context, val gson: Gson) : RangeStatsService {

    override fun getCurrentUserStats(tokenHeaderValue: String, range: String): Single<StatsServerModel> =
            gson.jsonToServiceModel(context, "stats-full")

}