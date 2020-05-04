package com.kondenko.pocketwaka.domain.stats.model

import android.graphics.Color

data class StatsItem(
        val name: String,
        val totalSeconds: Float?,
        val percent: Double?,
        var color: Int = Color.TRANSPARENT
)