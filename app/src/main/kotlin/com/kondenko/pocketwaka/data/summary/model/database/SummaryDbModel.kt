package com.kondenko.pocketwaka.data.summary.model.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kondenko.pocketwaka.data.CacheableModel
import com.kondenko.pocketwaka.domain.summary.model.SummaryUiModel

@Entity(tableName = "summary")
data class SummaryDbModel(
        @PrimaryKey
        val date: Long,
        val isAccountEmpty: Boolean? = null,
        override val isFromCache: Boolean = false,
        override val isEmpty: Boolean? = null,
        override val data: List<SummaryUiModel>
) : CacheableModel<List<SummaryUiModel>>

