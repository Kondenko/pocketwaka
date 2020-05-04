package com.kondenko.pocketwaka.data.summary.converters

import com.kondenko.pocketwaka.StatsRange
import com.kondenko.pocketwaka.data.android.DateFormatter
import com.kondenko.pocketwaka.data.summary.model.database.SummaryDbModel
import com.kondenko.pocketwaka.data.summary.model.server.SummaryData
import com.kondenko.pocketwaka.data.summary.repository.SummaryRepository
import com.kondenko.pocketwaka.domain.summary.model.SummaryUiModel
import com.kondenko.pocketwaka.domain.summary.usecase.GetAverage
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
        return convertTimeTracked(model).flatMap { uiModel ->
            val date: Long? = dateFormatter.parseDateParameter(model.range.date)
            val isAccountEmpty = uiModel.run { time.isEmpty() && percentDelta == null }
            val isTodayEmpty = model.grandTotal.totalSeconds == 0f && !isAccountEmpty
            date?.let {
                Maybe.just(SummaryDbModel(
                        date = it,
                        isAccountEmpty = isAccountEmpty,
                        isFromCache = false,
                        isEmpty = isTodayEmpty,
                        data = listOf(uiModel)
                ))
            } ?: Maybe.empty()
        }
    }

    private fun convertTimeTracked(model: SummaryData): Maybe<SummaryUiModel.TimeTracked> {
        val totalSeconds = model.grandTotal.totalSeconds.roundToInt()
        val averageSecondsSingle = getAverage(averageRange)
        return averageSecondsSingle.map { averageSeconds ->
            val formattedTime = dateFormatter.secondsToHumanReadableTime(totalSeconds.toLong())
            val averageDelta = getAverageDelta(totalSeconds, averageSeconds)
            SummaryUiModel.TimeTracked(formattedTime, averageDelta)
        }
    }

    private fun getAverageDelta(totalSeconds: Int, averageSec: Int) =
          if (averageSec != 0) totalSeconds * 100 / averageSec - 100 else null

}