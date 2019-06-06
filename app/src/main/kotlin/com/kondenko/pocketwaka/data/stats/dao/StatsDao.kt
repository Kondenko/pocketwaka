package com.kondenko.pocketwaka.data.stats.dao

import com.kondenko.pocketwaka.data.stats.dto.StatsDto
import io.reactivex.Completable
import io.reactivex.Single

class StatsDao {

    fun getCachedStats(range: String): Single<StatsDto> {
        return Single.error(NotImplementedError())
    }

    fun cacheStats(stats: StatsDto): Completable {
        return Completable.error(NotImplementedError())
    }

    fun clearCache(): Completable {
        return Completable.error(NotImplementedError())
    }

}