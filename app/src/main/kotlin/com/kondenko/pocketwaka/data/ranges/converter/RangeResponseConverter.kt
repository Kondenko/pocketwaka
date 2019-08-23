package com.kondenko.pocketwaka.data.ranges.converter

import android.content.Context
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.data.android.DateFormatter
import com.kondenko.pocketwaka.data.common.model.database.StatsEntity
import com.kondenko.pocketwaka.data.ranges.model.database.StatsDbModel
import com.kondenko.pocketwaka.data.ranges.model.server.Stats
import com.kondenko.pocketwaka.data.ranges.model.server.StatsServerModel
import com.kondenko.pocketwaka.data.ranges.repository.RangeStatsRepository
import com.kondenko.pocketwaka.domain.ranges.model.StatsItem
import com.kondenko.pocketwaka.domain.ranges.model.StatsUiModel
import com.kondenko.pocketwaka.utils.ColorProvider
import com.kondenko.pocketwaka.utils.date.DateProvider
import com.kondenko.pocketwaka.utils.extensions.notNull
import io.reactivex.Maybe
import io.reactivex.rxkotlin.toMaybe
import java.util.concurrent.TimeUnit
import kotlin.math.roundToLong

class RangeResponseConverter(
        private val context: Context,
        private val colorProvider: ColorProvider,
        private val dateFormatter: DateFormatter,
        private val dateProvider: DateProvider
) : (RangeStatsRepository.Params, StatsServerModel) -> Maybe<StatsDbModel> {

    private enum class StatsType {
        Editors, Languages, Projects, OperatingSystems
    }

    override fun invoke(params: RangeStatsRepository.Params, response: StatsServerModel): Maybe<StatsDbModel> =
            toDomainModel(params.range, response.stats).toMaybe()

    private fun toDomainModel(range: String, stats: Stats?): StatsDbModel? {
        if (stats == null) return null

        operator fun MutableList<StatsUiModel>.plusAssign(item: StatsUiModel?) {
            item?.let(this::add)
        }

        val list = arrayListOf<StatsUiModel>(
                StatsUiModel.Info(
                        stats.dailyAverage?.toLong()?.secondsToHumanReadableTime(),
                        stats.totalSeconds?.roundToLong()?.secondsToHumanReadableTime()
                )
        )

        list += stats.convertBestDay(stats.dailyAverage)
        list += stats.projects?.toDomainModel(StatsType.Projects)
        list += stats.languages?.toDomainModel(StatsType.Languages)
        list += stats.editors?.toDomainModel(StatsType.Editors)
        list += stats.operatingSystems?.toDomainModel(StatsType.OperatingSystems)

        return StatsDbModel(
                range = range,
                dateUpdated = dateProvider.getCurrentTimeMillis(),
                isFromCache = false,
                isEmpty = stats.totalSeconds == 0.0,
                data = list
        )
    }

    private fun List<StatsEntity>?.toDomainModel(statsType: StatsType): StatsUiModel.Stats? {
        val items = this?.filter { it.name != null }?.map { StatsItem(it.name!!, it.hours, it.minutes, it.percent) }
        return items
                ?.zip(colorProvider.provideColors(items)) { item, color -> item.copy(color = color) }
                ?.let { StatsUiModel.Stats(getCardTitle(statsType), it) }
    }

    private fun Stats.convertBestDay(dailyAverageSec: Int?): StatsUiModel.BestDay? = bestDay?.let { bestDay ->
        val date = bestDay.date?.let(dateFormatter::formatDateForDisplay)
        val timeSec = bestDay.totalSeconds?.roundToLong()
        val timeHumanReadable = timeSec?.secondsToHumanReadableTime()
        val percentAboveAverage = calculatePercentAboveAverage(timeSec, dailyAverageSec)
        if (notNull(date, timeHumanReadable, percentAboveAverage)) {
            StatsUiModel.BestDay(date!!, timeHumanReadable!!, percentAboveAverage!!)
        } else {
            null
        }
    }

    private fun calculatePercentAboveAverage(bestDayTotalSec: Long?, dailyAverageSec: Int?): Int? {
        if (bestDayTotalSec == null || dailyAverageSec == null) return null
        return (bestDayTotalSec * 100 / dailyAverageSec - 100).toInt()
    }

    private fun Long.secondsToHumanReadableTime(): String {
        val hours = TimeUnit.SECONDS.toHours(this)
        val minutes = TimeUnit.SECONDS.toMinutes(this) - TimeUnit.HOURS.toMinutes(hours)
        val templateHours = getHoursTemplate(hours.toInt())
        val templateMinutes = getMinutesTemplate(minutes.toInt())
        val timeBuilder = StringBuilder()
        if (hours > 0) timeBuilder.append(templateHours.format(hours)).append(' ')
        if (minutes > 0) timeBuilder.append(templateMinutes.format(minutes))
        return timeBuilder.toString()
    }

    private fun getHoursTemplate(hours: Int): String {
        return context.resources.getQuantityString(R.plurals.stats_time_format_hours, hours)
    }

    private fun getMinutesTemplate(minutes: Int): String {
        return context.resources.getQuantityString(R.plurals.stats_time_format_minutes, minutes)
    }

    private fun getCardTitle(statsType: StatsType): String = when (statsType) {
        StatsType.Projects -> context.getString(R.string.stats_card_header_projects)
        StatsType.Editors -> context.getString(R.string.stats_card_header_editors)
        StatsType.Languages -> context.getString(R.string.stats_card_header_languages)
        StatsType.OperatingSystems -> context.getString(R.string.stats_card_header_operating_systems)
    }

}