package com.kondenko.pocketwaka.data.summary.converters

import com.kondenko.pocketwaka.data.ModelConverter
import com.kondenko.pocketwaka.data.android.DateFormatter
import com.kondenko.pocketwaka.data.summary.dto.SummaryDto
import com.kondenko.pocketwaka.data.summary.model.SummaryData
import com.kondenko.pocketwaka.data.summary.repository.SummaryRepository
import com.kondenko.pocketwaka.domain.daily.model.SummaryUiModel

/**
 * Converts a summary of a single day to a DTO.
 */
class SummaryDataConverter(private val dateFormatter: DateFormatter) : ModelConverter<SummaryRepository.Params, SummaryData, SummaryDto> {

    override fun convert(model: SummaryData, param: SummaryRepository.Params): SummaryDto {
        val isEmpty = model.grandTotal.totalSeconds == 0f
        val uiModels = mutableListOf<SummaryUiModel>()
        uiModels += SummaryUiModel.TimeTracked(dateFormatter.formatDateForDisplay(model.grandTotal.totalSeconds), 0)
        return SummaryDto(model.range.date, false, isEmpty, uiModels)
    }

}