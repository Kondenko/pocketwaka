package com.kondenko.pocketwaka.data.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kondenko.pocketwaka.data.stats.dao.StatsDao
import com.kondenko.pocketwaka.data.stats.dto.StatsDto
import com.kondenko.pocketwaka.databaseVersion

@Database(entities = [StatsDto::class], version = databaseVersion)
abstract class AppDatabase : RoomDatabase() {
    abstract fun statsDao(): StatsDao
}