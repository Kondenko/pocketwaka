package com.kondenko.pocketwaka.data.ranges.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kondenko.pocketwaka.data.ranges.model.database.StatsDbModel
import io.reactivex.Completable
import io.reactivex.Maybe

@Dao
interface StatsDao {

    @Query("SELECT * FROM stats_cache WHERE range == :range")
    fun getCachedStats(range: String): Maybe<StatsDbModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun cacheStats(stats: StatsDbModel): Completable

    @Query("DELETE FROM stats_cache")
    fun clearCache(): Completable

}