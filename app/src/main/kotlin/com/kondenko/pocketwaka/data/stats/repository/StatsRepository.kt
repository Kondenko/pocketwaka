package com.kondenko.pocketwaka.data.stats.repository

import com.kondenko.pocketwaka.StatsRange
import com.kondenko.pocketwaka.data.CacheBackedRepository
import com.kondenko.pocketwaka.data.stats.dao.StatsDao
import com.kondenko.pocketwaka.data.stats.model.database.StatsDbModel
import com.kondenko.pocketwaka.data.stats.model.server.StatsServerModel
import com.kondenko.pocketwaka.data.stats.service.RangeStatsService
import io.reactivex.Completable
import io.reactivex.Maybe

class StatsRepository(
        private val service: RangeStatsService,
        private val dao: StatsDao
) : CacheBackedRepository<StatsRepository.Params, StatsServerModel, StatsDbModel>(
        serverDataProvider = { (tokenHeader, range) -> service.getCurrentUserStats(tokenHeader, range) },
        cachedDataProvider = { dao.getCachedStats(it.range) }
) {

    data class Params(val tokenHeader: String, val range: String)

    fun getDailyAverage(tokenHeader: String, averageRange: StatsRange): Maybe<Int> =
            service.getCurrentUserStats(tokenHeader, averageRange.value)
                    .flatMapMaybe {
                        it.stats?.dailyAverage?.let { Maybe.just(it) } ?: Maybe.empty()
                    }

    override fun cacheData(data: StatsDbModel): Completable = dao.cacheStats(data)

    override fun setIsFromCache(model: StatsDbModel, isFromCache: Boolean): StatsDbModel = model.copy(isFromCache = isFromCache)

}