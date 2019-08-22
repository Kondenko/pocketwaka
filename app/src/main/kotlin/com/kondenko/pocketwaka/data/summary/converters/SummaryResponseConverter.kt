package com.kondenko.pocketwaka.data.summary.converters

import com.kondenko.pocketwaka.data.summary.model.database.SummaryDbModel
import com.kondenko.pocketwaka.data.summary.model.database.SummaryRangeDto
import com.kondenko.pocketwaka.data.summary.model.server.Summary
import com.kondenko.pocketwaka.data.summary.model.server.SummaryData
import com.kondenko.pocketwaka.data.summary.repository.SummaryRepository
import com.kondenko.pocketwaka.utils.date.DateRangeString

/**
 * Converts [com.kondenko.pocketwaka.data.summary.service.SummaryService]'s response to a DTO.
 */
class SummaryResponseConverter(private val summaryDataConverter: (SummaryRepository.Params, SummaryData) -> SummaryDbModel)
    : (SummaryRepository.Params, Summary) -> SummaryRangeDto? {

    override fun invoke(param: SummaryRepository.Params, model: Summary): SummaryRangeDto {
        return model.summaryData
                .map { summaryDataConverter(param, it) }
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

