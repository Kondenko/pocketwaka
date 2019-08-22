package com.kondenko.pocketwaka.data.summary.converters

import com.kondenko.pocketwaka.data.ModelConverter
import com.kondenko.pocketwaka.data.summary.dto.SummaryDto
import com.kondenko.pocketwaka.data.summary.dto.SummaryRangeDto
import com.kondenko.pocketwaka.data.summary.model.Summary
import com.kondenko.pocketwaka.data.summary.model.SummaryData
import com.kondenko.pocketwaka.data.summary.repository.SummaryRepository
import com.kondenko.pocketwaka.utils.date.DateRangeString

/**
 * Converts [com.kondenko.pocketwaka.data.summary.service.SummaryService]'s response to a DTO.
 */
class SummaryResponseConverter(private val summaryDataConverter: ModelConverter<SummaryRepository.Params, SummaryData, SummaryDto>)
    : ModelConverter<SummaryRepository.Params, Summary, SummaryRangeDto?> {

    override fun convert(model: Summary, param: SummaryRepository.Params): SummaryRangeDto {
        return model.summaryData
                .map { summaryDataConverter.convert(it, param) }
                .let {
                    SummaryRangeDto(
                            range = DateRangeString(param.start, param.end),
                            isFromCache = false,
                            isEmpty = false,
                            data = it
                    )
                }
    }
}

