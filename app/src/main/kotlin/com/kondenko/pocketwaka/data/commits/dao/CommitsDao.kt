package com.kondenko.pocketwaka.data.commits.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kondenko.pocketwaka.cacheLifetimeCommitsMillis
import com.kondenko.pocketwaka.data.commits.model.CommitDbModel
import io.reactivex.Completable
import io.reactivex.Observable

@Dao
abstract class CommitsDao {

    @Query("""
            SELECT hash, project, branch, message, author_date, total_seconds
            FROM ${CommitDbModel.TABLE_NAME}, cache_update
            WHERE project=:project AND branch=:branch 
            AND table_name="${CommitDbModel.TABLE_NAME}" 
            AND (updated_at + :cacheLifetimeMillis) >= :currentTimeMillis
    """)
    abstract fun get(project: String, branch: String, currentTimeMillis: Long, cacheLifetimeMillis: Long = cacheLifetimeCommitsMillis): Observable<List<CommitDbModel>>

    open fun insert(list: List<CommitDbModel>, currentTimeSec: Long): Completable {
        return _insert(list).andThen(_onCommitsCacheUpdated(currentTimeSec))
    }

    // TODO See if it can be made protected
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun _insert(list: List<CommitDbModel>): Completable

    // (secondary) TODO Move into a separate DAO
    @Query("""INSERT OR REPLACE INTO cache_update VALUES("${CommitDbModel.TABLE_NAME}", :currentTimeMillis)""")
    abstract fun _onCommitsCacheUpdated(currentTimeMillis: Long): Completable

}