package com.kondenko.pocketwaka.data.summary.converters

import com.kondenko.pocketwaka.StatsRange
import com.kondenko.pocketwaka.data.android.DateFormatter
import com.kondenko.pocketwaka.data.summary.model.database.SummaryDbModel
import com.kondenko.pocketwaka.data.summary.model.server.SummaryData
import com.kondenko.pocketwaka.data.summary.repository.SummaryRepository
import com.kondenko.pocketwaka.domain.summary.model.SummaryUiModel
import com.kondenko.pocketwaka.domain.summary.usecase.GetAverage
import com.kondenko.pocketwaka.utils.date.DateRange
import com.kondenko.pocketwaka.utils.types.KOptional
import io.reactivex.Maybe
import kotlin.math.roundToInt

/**
 * Converts a summary of a single day to a DTO.
 */
class TimeTrackedConverter(
      private val getAverage: GetAverage,
      private val dateFormatter: DateFormatter
) : (SummaryRepository.Params, SummaryData) -> Maybe<SummaryDbModel> {

    private val averageRange = StatsRange.Month

    override fun invoke(param: SummaryRepository.Params, model: SummaryData): Maybe<SummaryDbModel> {
        return model.convertTimeTracked(showAverage = param.dateRange is DateRange.SingleDay).flatMap { uiModel ->
            val date = param.dateRange.hashCode().toLong()
            val isAccountEmpty = uiModel.run { time.isEmpty() && percentDelta == null }
            val isTodayEmpty = model.grandTotal.totalSeconds == 0f && !isAccountEmpty
            Maybe.just(SummaryDbModel(
                  date = date,
                  isAccountEmpty = isAccountEmpty,
                  isFromCache = false,
                  isEmpty = isTodayEmpty,
                  data = listOf(uiModel)
            ))
        }
    }

    private fun SummaryData.convertTimeTracked(showAverage: Boolean): Maybe<SummaryUiModel.TimeTracked> {
        val totalSeconds = grandTotal.totalSeconds.roundToInt()
        val averageSecondsSingle = getAverage(showAverage)
        return averageSecondsSingle.map { averageSeconds ->
            val formattedTime = dateFormatter.secondsToHumanReadableTime(totalSeconds.toLong())
            val averageDelta = averageSeconds.item?.let { getAverageDelta(totalSeconds, it) }
            SummaryUiModel.TimeTracked(formattedTime, averageDelta)
        }
    }

    private fun getAverage(showAverage: Boolean): Maybe<KOptional<Int>> =
          if (showAverage) {
              getAverage(averageRange)
                    .map { KOptional.of(it) }
                    .defaultIfEmpty(KOptional.empty())
          } else {
              Maybe.just(KOptional.empty())
          }

    private fun getAverageDelta(totalSeconds: Int, averageSec: Int) =
          if (averageSec != 0) totalSeconds * 100 / averageSec - 100 else null

}