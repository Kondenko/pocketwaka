package com.kondenko.pocketwaka.data.ranges.dto

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kondenko.pocketwaka.data.CacheableModel
import com.kondenko.pocketwaka.domain.ranges.model.StatsModel

@Entity(tableName = "stats_cache")
data class StatsDto(
        @PrimaryKey
        val range: String,
        @ColumnInfo(name = "date_updated")
        val dateUpdated: Long,
        override val isFromCache: Boolean = false,
        override val isEmpty: Boolean = false,
        override val data: List<StatsModel>
) : CacheableModel<List<StatsModel>>