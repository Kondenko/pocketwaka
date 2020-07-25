package com.kondenko.pocketwaka.data.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kondenko.pocketwaka.data.CacheUpdateEntry
import com.kondenko.pocketwaka.data.commits.dao.CommitsDao
import com.kondenko.pocketwaka.data.commits.model.CommitDbModel
import com.kondenko.pocketwaka.data.stats.converter.StatsListConverter
import com.kondenko.pocketwaka.data.stats.dao.StatsDao
import com.kondenko.pocketwaka.data.stats.model.database.StatsDbModel
import com.kondenko.pocketwaka.data.summary.converters.SummaryListConverter
import com.kondenko.pocketwaka.data.summary.dao.SummaryDao
import com.kondenko.pocketwaka.data.summary.model.database.SummaryDbModel
import com.kondenko.pocketwaka.databaseVersion


@Database(entities = [StatsDbModel::class, SummaryDbModel::class, CommitDbModel::class, CacheUpdateEntry::class], version = databaseVersion)
@TypeConverters(StatsListConverter::class, SummaryListConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun statsDao(): StatsDao
    abstract fun summaryDao(): SummaryDao
    abstract fun commitsDao(): CommitsDao
}