package com.kondenko.pocketwaka.domain.stats

import com.kondenko.pocketwaka.data.stats.repository.StatsRepository
import com.kondenko.pocketwaka.domain.UseCaseSingle
import com.kondenko.pocketwaka.domain.auth.GetTokenHeaderValue
import com.kondenko.pocketwaka.domain.stats.model.BestDay
import com.kondenko.pocketwaka.domain.stats.model.StatsItem
import com.kondenko.pocketwaka.domain.stats.model.StatsModel
import com.kondenko.pocketwaka.utils.ColorProvider
import com.kondenko.pocketwaka.utils.SchedulerContainer
import com.kondenko.pocketwaka.utils.TimeProvider
import io.reactivex.Single
import io.reactivex.rxkotlin.zipWith
import java.util.concurrent.TimeUnit


class GetStats(
        schedulers: SchedulerContainer,
        private val timeProvider: TimeProvider,
        private val colorProvider: ColorProvider,
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
            .map {
                val bestDayModel = it.bestDay?.let {
                    // Introducing these constants to ensure the needed values' immutability
                    val date = it.date
                    val timeSec = it.totalSeconds
                    if (date != null && timeSec != null) BestDay(date, secondsToHumanReadableTime(timeSec))
                    else null
                }
                StatsModel(
                        bestDayModel,
                        secondsToHumanReadableTime(it.dailyAverage?:0),
                        secondsToHumanReadableTime(it.totalSeconds?:0),
                        it.projects?.map { StatsItem(it.hours, it.minutes, it.name, it.percent, it.totalSeconds) }?.provideColors(),
                        it.languages?.map { StatsItem(it.hours, it.minutes, it.name, it.percent, it.totalSeconds) }?.provideColors(),
                        it.editors?.map { StatsItem(it.hours, it.minutes, it.name, it.percent, it.totalSeconds) }?.provideColors(),
                        it.operatingSystems?.map { StatsItem(it.hours, it.minutes, it.name, it.percent, it.totalSeconds) }?.provideColors(),
                        it.range,
                        timeProvider.getCurrentTimeMillis(),
                        it.totalSeconds == 0
                )
            }

    private fun List<StatsItem>.provideColors(): List<StatsItem> {
        val colors = colorProvider.provideColors(this)
        this.zip(colors) { item, color -> item.color = color }
        return this
    }

    private fun secondsToHumanReadableTime(totalSeconds: Int): String {
        val hours = TimeUnit.SECONDS.toHours(totalSeconds.toLong())
        val minutes = TimeUnit.SECONDS.toMinutes(totalSeconds.toLong()) - TimeUnit.HOURS.toMinutes(hours)
        val templateHours = statsRepository.getHoursTemplate(hours.toInt())
        val templateMinutes = statsRepository.getMinutesTemplate(minutes.toInt())
        val timeBuilder = StringBuilder()
        if (hours > 0) timeBuilder.append(templateHours.format(hours)).append(' ')
        if (minutes > 0) timeBuilder.append(templateMinutes.format(minutes))
        return timeBuilder.toString()
    }


}