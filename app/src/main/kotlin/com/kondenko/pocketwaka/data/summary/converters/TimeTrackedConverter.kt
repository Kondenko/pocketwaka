package com.kondenko.pocketwaka.data.summary.converters

import com.kondenko.pocketwaka.StatsRange
import com.kondenko.pocketwaka.data.android.DateFormatter
import com.kondenko.pocketwaka.data.summary.model.database.SummaryDbModel
import com.kondenko.pocketwaka.data.summary.model.server.SummaryData
import com.kondenko.pocketwaka.data.summary.repository.SummaryRepository
import com.kondenko.pocketwaka.domain.daily.model.SummaryUiModel
import com.kondenko.pocketwaka.domain.daily.usecase.GetAverage
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
        val isEmpty = model.grandTotal.totalSeconds == 0f
        return convertTimeTracked(model).map {
            SummaryDbModel(model.range.date, false, isEmpty, listOf(it))
        }
    }

    private fun convertTimeTracked(model: SummaryData): Maybe<SummaryUiModel> {
        val totalSeconds = model.grandTotal.totalSeconds.roundToInt()
        val averageSecondsSingle = getAverage(averageRange)
        return averageSecondsSingle.map { averageSeconds ->
            val averageDelta = getAverageDelta(totalSeconds, averageSeconds)
            val formattedTime = dateFormatter.secondsToHumanReadableTime(totalSeconds.toLong())
            SummaryUiModel.TimeTracked(formattedTime, averageDelta)
        }
    }

    private fun getAverageDelta(totalSeconds: Int, averageSec: Int): Int {
        return totalSeconds * 100 / averageSec - 100
    }


}