package com.kondenko.pocketwaka.data.summary.dto

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kondenko.pocketwaka.data.CacheableModel
import com.kondenko.pocketwaka.domain.daily.model.SummaryUiModel
import com.kondenko.pocketwaka.utils.date.DateRangeString

@Entity(tableName = "summary_range")
data class SummaryRangeDto(
        @PrimaryKey
        val range: DateRangeString,
        override val isFromCache: Boolean = false,
        override val isEmpty: Boolean = false,
        override val data: List<SummaryDto>
) : CacheableModel<List<SummaryDto>>

@Entity(tableName = "summary")
data class SummaryDto(
        @PrimaryKey
        val date: String,
        override val isFromCache: Boolean = false,
        override val isEmpty: Boolean = false,
        override val data: List<SummaryUiModel>
) : CacheableModel<List<SummaryUiModel>>

