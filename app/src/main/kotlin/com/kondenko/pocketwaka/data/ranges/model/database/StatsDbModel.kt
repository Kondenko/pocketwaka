package com.kondenko.pocketwaka.data.ranges.model.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kondenko.pocketwaka.data.CacheableModel
import com.kondenko.pocketwaka.domain.ranges.model.StatsUiModel

@Entity(tableName = "stats_cache")
data class StatsDbModel(
        @PrimaryKey
        val range: String,
        @ColumnInfo(name = "date_updated")
        val dateUpdated: Long,
        override val isFromCache: Boolean = false,
        override val isEmpty: Boolean = false,
        override val data: List<StatsUiModel>
) : CacheableModel<List<StatsUiModel>>