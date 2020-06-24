package com.kondenko.pocketwaka.data.commits.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kondenko.pocketwaka.data.commits.model.CommitDbModel
import io.reactivex.Completable
import io.reactivex.Observable

@Dao
abstract class CommitsDao {

/*
    @Query("""
            SELECT hash, project, branch, message, author_date, total_seconds
            FROM ${CommitDbModel.TABLE_NAME} 
            WHERE project=:project AND branch=:branch
    """)
*/
//    , cache_update
    //  AND table_name="${CommitDbModel.TABLE_NAME}"  AND (updated_at + :cacheLifetimeMillis <= DATE()) TODO Bring back
    // , cacheLifetimeMillis: Long = cacheLifetimeCommits
    @Query("SELECT * FROM commits")
    abstract fun get(): Observable<List<CommitDbModel>>
//    abstract fun get(project: String, branch: String): Observable<List<CommitDbModel>>

    open fun insert(list: List<CommitDbModel>): Completable {
        return _insert(list).andThen(_onCommitsCacheUpdated())
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun _insert(list: List<CommitDbModel>): Completable

    // TODO Move into a separate DAO
    @Query("""INSERT INTO cache_update VALUES("${CommitDbModel.TABLE_NAME}", DATE())""")
    abstract fun _onCommitsCacheUpdated(): Completable

}