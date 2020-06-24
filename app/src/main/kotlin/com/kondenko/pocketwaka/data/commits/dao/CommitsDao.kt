package com.kondenko.pocketwaka.data.commits.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.kondenko.pocketwaka.data.commits.model.CommitDbModel
import io.reactivex.Completable
import io.reactivex.Observable

@Dao
abstract class CommitsDao {

    @Query("""
            SELECT hash, project, branch, message, author_date, total_seconds
            FROM ${CommitDbModel.TABLE_NAME}, cache_update 
            WHERE project=:project AND branch=:branch
    """)
    //  AND table_name="${CommitDbModel.TABLE_NAME}"  AND (updated_at + :cacheLifetimeMillis <= DATE()) TODO Bring back
    // , cacheLifetimeMillis: Long = cacheLifetimeCommits
    abstract fun get(project: String, branch: String): Observable<CommitDbModel>

    @Transaction
    open fun insert(list: List<CommitDbModel>) {
        _insert(list)
        onCommitsCacheUpdated()
    }

    @Insert
    abstract fun _insert(list: List<CommitDbModel>): Completable

    @Query("""INSERT INTO cache_update VALUES("${CommitDbModel.TABLE_NAME}", DATE())""")
    abstract fun onCommitsCacheUpdated(): Completable

}