package com.kondenko.pocketwaka.domain.stats

import com.kondenko.pocketwaka.data.android.DateFormatter
import com.kondenko.pocketwaka.data.stats.model.Stats
import com.kondenko.pocketwaka.data.stats.repository.StatsRepository
import com.kondenko.pocketwaka.domain.UseCaseSingle
import com.kondenko.pocketwaka.domain.auth.GetTokenHeaderValue
import com.kondenko.pocketwaka.domain.stats.model.BestDay
import com.kondenko.pocketwaka.domain.stats.model.StatsItem
import com.kondenko.pocketwaka.domain.stats.model.StatsModel
import com.kondenko.pocketwaka.utils.ColorProvider
import com.kondenko.pocketwaka.utils.SchedulerContainer
import com.kondenko.pocketwaka.utils.TimeProvider
import com.kondenko.pocketwaka.utils.notNull
import io.reactivex.Single
import io.reactivex.rxkotlin.zipWith
import java.util.concurrent.TimeUnit
import kotlin.math.roundToLong


class GetStats(
        schedulers: SchedulerContainer,
        private val timeProvider: TimeProvider,
        private val colorProvider: ColorProvider,
        private val dateFormatter: DateFormatter,
        private val getTokenHeader: GetTokenHeaderValue,
        private val statsRepository: StatsRepository
) : UseCaseSingle<String, StatsModel>(schedulers) {

    private val timerSingle = Single.timer(100, TimeUnit.MILLISECONDS)

    override fun build(range: String?): Single<StatsModel> = getTokenHeader.build()
            .flatMap { header -> statsRepository.getStats(header, range!!) }
            /*
            When the user tries to refresh the data it refreshes so quickly
            that the progress bar doesn't have the time to be shown and hidden again.
            We add a tiny delay to the loading process so users
            actually see that the loading happens.
            */
            .zipWith(timerSingle.apply { repeat() }) { stats, _ -> stats }
            .map { it.stats }
            .map(::toDomainModel)

    private fun toDomainModel(stats: Stats) = StatsModel(
            stats.convertBestDay(stats.dailyAverage),
            stats.dailyAverage?.toLong()?.secondsToHumanReadableTime(),
            stats.totalSeconds?.roundToLong()?.secondsToHumanReadableTime(),
            stats.projects?.map { StatsItem(it.hours, it.minutes, it.name, it.percent) }?.provideColors(),
            stats.languages?.map { StatsItem(it.hours, it.minutes, it.name, it.percent) }?.provideColors(),
            stats.editors?.map { StatsItem(it.hours, it.minutes, it.name, it.percent) }?.provideColors(),
            stats.operatingSystems?.map { StatsItem(it.hours, it.minutes, it.name, it.percent) }?.provideColors(),
            stats.range,
            timeProvider.getCurrentTimeMillis(),
            stats.totalSeconds == 0.0
    )

    private fun Stats.convertBestDay(dailyAverageSec: Int?): BestDay? = bestDay?.let { bestDay ->
        val date = bestDay.date?.let(dateFormatter::reformatBestDayDate)
        val timeSec = bestDay.totalSeconds?.roundToLong()
        val timeHumanReadable = timeSec?.secondsToHumanReadableTime()
        val percentAboveAverage = calculatePercentAboveAverage(timeSec, dailyAverageSec)
        if (notNull(date, timeHumanReadable, percentAboveAverage)) {
            BestDay(date!!, timeHumanReadable!!, percentAboveAverage!!)
        } else {
            null
        }
    }

    private fun calculatePercentAboveAverage(bestDayTotalSec: Long?, dailyAverageSec: Int?): Int? {
        if (bestDayTotalSec == null || dailyAverageSec == null) return null
        return (bestDayTotalSec * 100 / dailyAverageSec - 100).toInt()
    }

    private fun List<StatsItem>.provideColors(): List<StatsItem> = apply {
        zip(colorProvider.provideColors(this)) { item, color -> item.color = color }
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