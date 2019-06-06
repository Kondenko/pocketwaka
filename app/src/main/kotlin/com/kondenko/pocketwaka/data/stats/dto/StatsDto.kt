package com.kondenko.pocketwaka.data.stats.dto

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kondenko.pocketwaka.domain.stats.model.StatsModel

@Entity(tableName = "stats_cache")
data class StatsDto(
        @PrimaryKey
        val range: String,
        @ColumnInfo(name = "date_updated")
        val dateUpdated: Long,
        @Embedded
        val stats: ArrayList<StatsModel>
)