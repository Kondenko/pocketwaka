package com.kondenko.pocketwaka.data.stats.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kondenko.pocketwaka.data.stats.dto.StatsDto
import io.reactivex.Completable
import io.reactivex.Maybe

@Dao
interface StatsDao {

    @Query("SELECT * FROM stats_cache WHERE range == :range")
    fun getCachedStats(range: String): Maybe<StatsDto>

    @Insert
    fun cacheStats(stats: StatsDto): Completable

    @Query("DELETE FROM stats_cache")
    fun clearCache(): Completable

}