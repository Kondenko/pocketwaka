package com.kondenko.pocketwaka.domain.stats

import com.kondenko.pocketwaka.dagger.PerScreen
import com.kondenko.pocketwaka.data.stats.repository.StatsRepository
import com.kondenko.pocketwaka.domain.UseCaseSingle
import com.kondenko.pocketwaka.domain.auth.GetTokenHeaderValue
import com.kondenko.pocketwaka.domain.stats.model.BestDay
import com.kondenko.pocketwaka.domain.stats.model.StatsItem
import com.kondenko.pocketwaka.domain.stats.model.StatsModel
import com.kondenko.pocketwaka.utils.ColorProvider
import com.kondenko.pocketwaka.utils.SchedulerContainer
import io.reactivex.Single
import io.reactivex.rxkotlin.zipWith
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@PerScreen
class GetStats @Inject constructor(
        schedulers: SchedulerContainer,
        private val colorProvider: ColorProvider,
        private val getTokenHeader: GetTokenHeaderValue,
        private val statsRepository: StatsRepository
) : UseCaseSingle<String, StatsModel>(schedulers) {

    private val timerSingle = Single.timer(100, TimeUnit.MILLISECONDS)

    override fun build(range: String?) = getTokenHeader.build()
            .flatMap { header -> statsRepository.getStats(header, range!!) }
            /*
            When the user tries to refresh the data it refreshes so quickly
            that the progress bar doesn't have the time to be shown and hidden again.
            We add a tiny delay to the loading process so users
            actually see that the loading happens.
            */
            .zipWith(timerSingle.apply { repeat() }) { stats, time -> stats }
            .map { it.stats }
            .map {
                val bestDayUi = it.bestDay?.let {
                    // Introducing these constants to ensure the needed values' immutability
                    val date = it.date
                    val timeSec = it.totalSeconds
                    if (date != null && timeSec != null) BestDay(date, secondsToDate(timeSec))
                    else null
                }
                StatsModel(
                        bestDayUi,
                        it.humanReadableDailyAverage,
                        it.humanReadableTotal,
                        it.projects?.map { StatsItem(it.hours, it.minutes, it.name, it.percent, it.totalSeconds) }?.provideColors(),
                        it.languages?.map { StatsItem(it.hours, it.minutes, it.name, it.percent, it.totalSeconds) }?.provideColors(),
                        it.editors?.map { StatsItem(it.hours, it.minutes, it.name, it.percent, it.totalSeconds) }?.provideColors(),
                        it.operatingSystems?.map { StatsItem(it.hours, it.minutes, it.name, it.percent, it.totalSeconds) }?.provideColors(),
                        it.range,
                        System.currentTimeMillis(),
                        it.totalSeconds == 0
                )
            }
            /*
            .map {
                if (BuildConfig.DEBUG) {
                    val random = Random().nextInt(3)
                    when (random) {
                        1 -> throw RuntimeException("Intended error")
                        2 -> it.copy(isEmpty = true)
                        else -> it
                    }
                } else it
            }
            */


    private fun List<StatsItem>.provideColors(): List<StatsItem> {
        val colors = colorProvider.provideColors(this)
        this.zip(colors) { item, color -> item.color = color }
        return this
    }

    private fun secondsToDate(totalSeconds: Int): String {
        val hours = TimeUnit.SECONDS.toHours(totalSeconds.toLong())
        val minutes = TimeUnit.SECONDS.toMinutes(totalSeconds.toLong()) - TimeUnit.HOURS.toMinutes(hours)
        val templateHours = statsRepository.getBestDayHoursTemplate(hours.toInt())
        val templateMinutes = statsRepository.getBestDayMinutesTemplate(minutes.toInt())
        val timeBuilder = StringBuilder()
        if (hours > 0) timeBuilder.append(templateHours.format(hours)).append(' ')
        if (minutes > 0) timeBuilder.append(templateMinutes.format(minutes))
        return timeBuilder.toString()
    }


}