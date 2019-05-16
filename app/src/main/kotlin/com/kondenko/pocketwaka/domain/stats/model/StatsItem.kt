package com.kondenko.pocketwaka.domain.stats.model

import android.graphics.Color
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StatsItem(
        val hours: Int?,
        val minutes: Int?,
        val name: String?,
        val percent: Double?,
        var color: Int = Color.TRANSPARENT
) : Parcelable