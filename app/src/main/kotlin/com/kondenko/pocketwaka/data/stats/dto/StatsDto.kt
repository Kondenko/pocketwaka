package com.kondenko.pocketwaka.data.stats.dto

import com.kondenko.pocketwaka.data.stats.model.Stats


data class StatsDto(
        val range: String,
        val dateUpdated: Long,
        val stats: Stats
)