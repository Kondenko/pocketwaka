package com.kondenko.pocketwaka.api

enum class StatsRange(val value: String) {
    WEEK("last_7_days"),
    MONTH("last_30_days"),
    HALF_YEAR("last_6_months"),
    YEAR("last_year")
}