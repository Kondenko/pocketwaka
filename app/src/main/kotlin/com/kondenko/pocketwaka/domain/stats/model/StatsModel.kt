package com.kondenko.pocketwaka.domain.stats.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StatsModel(
        val bestDay: BestDay?,
        val humanReadableDailyAverage: String?,
        val humanReadableTotal: String?,
        val projects: List<StatsItem>?,
        val languages: List<StatsItem>?,
        val editors: List<StatsItem>?,
        val operatingSystems: List<StatsItem>?,
        val range: String?,
        val lastUpdated: Long,
        val isEmpty: Boolean
) : Parcelable
