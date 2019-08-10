package com.kondenko.pocketwaka.data.ranges.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kondenko.pocketwaka.data.ranges.dto.StatsDto
import io.reactivex.Completable
import io.reactivex.Maybe

@Dao
interface StatsDao {

    @Query("SELECT * FROM stats_cache WHERE range == :range")
    fun getCachedStats(range: String): Maybe<StatsDto>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun cacheStats(stats: StatsDto): Completable

    @Query("DELETE FROM stats_cache")
    fun clearCache(): Completable

}