package com.kondenko.pocketwaka.domain.ranges.model

import android.graphics.Color

data class StatsItem(
        val name: String,
        val hours: Int?,
        val minutes: Int?,
        val percent: Double?,
        var color: Int = Color.TRANSPARENT
)