package com.kondenko.pocketwaka.data.summary.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kondenko.pocketwaka.data.summary.model.database.SummaryDbModel
import io.reactivex.Completable
import io.reactivex.Maybe

@Dao
interface SummaryDao {

    @Query("SELECT * FROM summary WHERE date NOTNULL AND date BETWEEN :start AND :end")
    fun getSummaries(start: Long, end: Long): Maybe<List<SummaryDbModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun cacheSummary(summary: SummaryDbModel): Completable

    @Query("DELETE FROM summary")
    fun clearCache(): Completable

}