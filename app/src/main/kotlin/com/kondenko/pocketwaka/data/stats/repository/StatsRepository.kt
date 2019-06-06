package com.kondenko.pocketwaka.data.stats.repository

import android.content.Context
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.data.stats.dao.StatsDao
import com.kondenko.pocketwaka.data.stats.dto.StatsDto
import com.kondenko.pocketwaka.data.stats.model.Stats
import com.kondenko.pocketwaka.data.stats.service.StatsService
import com.kondenko.pocketwaka.utils.TimeProvider
import io.reactivex.Observable
import io.reactivex.Single

class StatsRepository(
        private val context: Context,
        private val service: StatsService,
        private val dao: StatsDao,
        private val timeProvider: TimeProvider
) {

    enum class StatsType {
        Editors, Languages, Projects, OperatingSystems
    }

    fun getStats(tokenHeader: String, range: String, onLoadedFromServer: (StatsDto) -> Unit): Observable<StatsDto> {
        val cache = getStatsFromCache(range)
        val server = getStatsFromServer(tokenHeader, range)
                .doOnSuccess { onLoadedFromServer(it) }
        return cache.concatWith(server).toObservable()
    }

    fun cacheStats(stats: StatsDto) = dao.cacheStats(stats)

    private fun getStatsFromServer(tokenHeader: String, range: String): Single<StatsDto> =
            service.getCurrentUserStats(tokenHeader, range)
                    .map { it.stats.toDto(range) }

    private fun getStatsFromCache(range: String): Single<StatsDto> = dao.getCachedStats(range)

    private fun Stats.toDto(range: String) = StatsDto(range, timeProvider.getCurrentTimeMillis(), this)

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