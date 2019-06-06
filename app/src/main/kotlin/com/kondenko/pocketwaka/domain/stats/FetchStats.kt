package com.kondenko.pocketwaka.domain.stats

import com.kondenko.pocketwaka.data.android.DateFormatter
import com.kondenko.pocketwaka.data.stats.model.Stats
import com.kondenko.pocketwaka.data.stats.repository.StatsRepository
import com.kondenko.pocketwaka.domain.UseCaseObservable
import com.kondenko.pocketwaka.domain.auth.GetTokenHeaderValue
import com.kondenko.pocketwaka.domain.stats.model.StatsItem
import com.kondenko.pocketwaka.domain.stats.model.StatsModel
import com.kondenko.pocketwaka.utils.ColorProvider
import com.kondenko.pocketwaka.utils.SchedulersContainer
import com.kondenko.pocketwaka.utils.notNull
import io.reactivex.Observable
import java.util.concurrent.TimeUnit
import kotlin.math.roundToLong


class FetchStats(
        schedulers: SchedulersContainer,
        private val colorProvider: ColorProvider,
        private val dateFormatter: DateFormatter,
        private val getTokenHeader: GetTokenHeaderValue,
        private val statsRepository: StatsRepository
) : UseCaseObservable<String, List<StatsModel>>(schedulers) {

    override fun build(range: String?): Observable<List<StatsModel>> {
        return getTokenHeader.build()
                .flatMapObservable { header ->
                    statsRepository.getStats(header, range!!, onLoadedFromServer = {
                        statsRepository.cacheStats(it)
                    })
                }
                .map { this.toDomainModel(it.stats, it.dateUpdated) }
    }

    private fun toDomainModel(stats: Stats, dateUpdated: Long): List<StatsModel> {
        operator fun MutableList<StatsModel>.plusAssign(item: StatsModel?) {
            item?.let(this::add)
        }

        val list = mutableListOf<StatsModel>(
                StatsModel.Info(
                        stats.dailyAverage?.toLong()?.secondsToHumanReadableTime(),
                        stats.totalSeconds?.roundToLong()?.secondsToHumanReadableTime()
                )
        )
        list += stats.convertBestDay(stats.dailyAverage)
        list += stats.projects?.toDomainModel(StatsRepository.StatsType.Projects)
        list += stats.languages?.toDomainModel(StatsRepository.StatsType.Languages)
        list += stats.editors?.toDomainModel(StatsRepository.StatsType.Editors)
        list += stats.operatingSystems?.toDomainModel(StatsRepository.StatsType.OperatingSystems)
        list += StatsModel.Metadata(
                lastUpdated = dateUpdated,
                isEmpty = stats.totalSeconds == 0.0,
                range = stats.range
        )
        return list
    }

    private fun Stats.convertBestDay(dailyAverageSec: Int?): StatsModel.BestDay? = bestDay?.let { bestDay ->
        val date = bestDay.date?.let(dateFormatter::reformatBestDayDate)
        val timeSec = bestDay.totalSeconds?.roundToLong()
        val timeHumanReadable = timeSec?.secondsToHumanReadableTime()
        val percentAboveAverage = calculatePercentAboveAverage(timeSec, dailyAverageSec)
        if (notNull(date, timeHumanReadable, percentAboveAverage)) {
            StatsModel.BestDay(date!!, timeHumanReadable!!, percentAboveAverage!!)
        } else {
            null
        }
    }

    private fun calculatePercentAboveAverage(bestDayTotalSec: Long?, dailyAverageSec: Int?): Int? {
        if (bestDayTotalSec == null || dailyAverageSec == null) return null
        return (bestDayTotalSec * 100 / dailyAverageSec - 100).toInt()
    }

    private fun List<com.kondenko.pocketwaka.data.stats.model.StatsItem>?.toDomainModel(statsType: StatsRepository.StatsType): StatsModel.Stats? {
        val items = this?.map { StatsItem(it.hours, it.minutes, it.name, it.percent) }
        return items
                ?.zip(colorProvider.provideColors(items)) { item, color -> item.copy(color = color) }
                ?.let { StatsModel.Stats(statsRepository.getCardTitle(statsType), it) }
    }


    private fun Long.secondsToHumanReadableTime(): String {
        val hours = TimeUnit.SECONDS.toHours(this)
        val minutes = TimeUnit.SECONDS.toMinutes(this) - TimeUnit.HOURS.toMinutes(hours)
        val templateHours = statsRepository.getHoursTemplate(hours.toInt())
        val templateMinutes = statsRepository.getMinutesTemplate(minutes.toInt())
        val timeBuilder = StringBuilder()
        if (hours > 0) timeBuilder.append(templateHours.format(hours)).append(' ')
        if (minutes > 0) timeBuilder.append(templateMinutes.format(minutes))
        return timeBuilder.toString()
    }

}