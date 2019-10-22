package com.kondenko.pocketwaka.data.stats.converter

import android.content.Context
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.data.android.ColorProvider
import com.kondenko.pocketwaka.data.android.DateFormatter
import com.kondenko.pocketwaka.data.common.model.database.StatsEntity
import com.kondenko.pocketwaka.data.stats.model.database.StatsDbModel
import com.kondenko.pocketwaka.data.stats.model.server.Stats
import com.kondenko.pocketwaka.data.stats.model.server.StatsServerModel
import com.kondenko.pocketwaka.data.stats.repository.StatsRepository
import com.kondenko.pocketwaka.domain.stats.model.StatsItem
import com.kondenko.pocketwaka.domain.stats.model.StatsUiModel
import com.kondenko.pocketwaka.utils.date.DateProvider
import com.kondenko.pocketwaka.utils.extensions.notNull
import io.reactivex.Maybe
import io.reactivex.rxkotlin.toMaybe
import kotlin.math.roundToLong

class StatsResponseConverter(
      private val context: Context,
      private val colorProvider: ColorProvider,
      private val dateProvider: DateProvider,
      private val dateFormatter: DateFormatter
) : (StatsRepository.Params, StatsServerModel) -> Maybe<StatsDbModel> {

    private enum class StatsType {
        Editors, Languages, Projects, OperatingSystems
    }

    override fun invoke(params: StatsRepository.Params, response: StatsServerModel): Maybe<StatsDbModel> =
          toDomainModel(params.range, response.stats).toMaybe()

    private fun toDomainModel(range: String, stats: Stats?): StatsDbModel? {
        if (stats == null) return null

        operator fun MutableList<StatsUiModel>.plusAssign(item: StatsUiModel?) {
            item?.let(this::add)
        }

        val list = arrayListOf<StatsUiModel>(
              StatsUiModel.Info(
                    stats.dailyAverage?.toLong()?.let(dateFormatter::secondsToHumanReadableTime),
                    stats.totalSeconds?.roundToLong()?.let(dateFormatter::secondsToHumanReadableTime)
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
        return colorProvider.provideColors(items)?.let { StatsUiModel.Stats(getCardTitle(statsType), it) }
    }

    private fun Stats.convertBestDay(dailyAverageSec: Int?): StatsUiModel.BestDay? = bestDay?.let { bestDay ->
        val date = bestDay.date?.let(dateFormatter::formatDateForDisplay)
        val timeSec = bestDay.totalSeconds?.roundToLong()
        val timeHumanReadable = timeSec?.let(dateFormatter::secondsToHumanReadableTime)
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

    private fun getCardTitle(statsType: StatsType): String = when (statsType) {
        StatsType.Projects -> context.getString(R.string.stats_card_header_projects)
        StatsType.Editors -> context.getString(R.string.stats_card_header_editors)
        StatsType.Languages -> context.getString(R.string.stats_card_header_languages)
        StatsType.OperatingSystems -> context.getString(R.string.stats_card_header_operating_systems)
    }

}