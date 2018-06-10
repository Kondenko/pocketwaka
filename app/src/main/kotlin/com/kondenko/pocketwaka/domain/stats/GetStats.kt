package com.kondenko.pocketwaka.domain.stats

import com.kondenko.pocketwaka.dagger.PerApp
import com.kondenko.pocketwaka.dagger.PerScreen
import com.kondenko.pocketwaka.data.stats.repository.StatsRepository
import com.kondenko.pocketwaka.domain.UseCaseSingle
import com.kondenko.pocketwaka.domain.auth.GetTokenHeaderValue
import com.kondenko.pocketwaka.domain.stats.model.BestDay
import com.kondenko.pocketwaka.domain.stats.model.StatsItem
import com.kondenko.pocketwaka.domain.stats.model.StatsModel
import com.kondenko.pocketwaka.utils.ColorProvider
import com.kondenko.pocketwaka.utils.SchedulerContainer
import javax.inject.Inject

@PerScreen
class GetStats @Inject constructor(
        schedulers: SchedulerContainer,
        private val colorProvider: ColorProvider,
        private val getTokenHeader: GetTokenHeaderValue,
        private val statsRepository: StatsRepository
) : UseCaseSingle<String, StatsModel>(schedulers) {

    override fun build(range: String?) = getTokenHeader.build()
            .flatMap { header -> statsRepository.getStats(header, range!!) }
            .map { it.stats }
            .map {
                StatsModel(
                        BestDay(it.bestDay?.date, it.bestDay?.totalSeconds),
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
        this.zip(colors) { item, color ->
            item.color = color
        }
        return this
    }

}