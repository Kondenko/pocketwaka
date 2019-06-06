package com.kondenko.pocketwaka.data.stats.repository

import android.content.Context
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.data.stats.service.StatsService

class StatsRepository(private val context: Context, private val service: StatsService) {

    enum class StatsType {
        Editors, Languages, Projects, OperatingSystems
    }

    fun getStats(tokenHeader: String, range: String) = service.getCurrentUserStats(tokenHeader, range)

    fun getHoursTemplate(hours: Int): String {
        return context.resources.getQuantityString(R.plurals.stats_time_format_hours, hours)
    }

    fun getMinutesTemplate(minutes: Int): String {
        return context.resources.getQuantityString(R.plurals.stats_time_format_minutes, minutes)
    }

    fun getCardTitle(statsType: StatsType): String = when (statsType) {
        StatsType.Projects -> context.getString(R.string.stats_card_header_projects)
        StatsType.Editors -> context.getString(R.string.stats_card_header_editors)
        StatsType.Languages -> context.getString(R.string.stats_card_header_languages)
        StatsType.OperatingSystems -> context.getString(R.string.stats_card_header_operating_systems)
    }

}