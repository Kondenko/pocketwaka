package com.kondenko.pocketwaka.data.daily.converters

import com.kondenko.pocketwaka.data.ModelConverter
import com.kondenko.pocketwaka.data.daily.dto.SummaryDto
import com.kondenko.pocketwaka.data.daily.dto.SummaryRangeDto
import com.kondenko.pocketwaka.data.daily.model.Summary
import com.kondenko.pocketwaka.data.daily.model.SummaryData
import com.kondenko.pocketwaka.data.daily.repository.SummaryRepository

class SummaryResponseConverter(private val summaryDataConverter: ModelConverter<SummaryRepository.Params, SummaryData, SummaryDto>)
    : ModelConverter<SummaryRepository.Params, Summary, SummaryRangeDto> {

    override fun convert(model: Summary, param: SummaryRepository.Params): SummaryRangeDto {
        return model.summaryData
                .map { summaryDataConverter.convert(it, param) }
                .let {
                    SummaryRangeDto(
                            range = param.dateRangeString,
                            isFromCache = false,
                            isEmpty = false,
                            data = it
                    )
                }
    }
}

class SummaryDataConverter : ModelConverter<SummaryRepository.Params, SummaryData, SummaryDto> {

    /**
     * TODO Implement conversion
     */
    override fun convert(model: SummaryData, param: SummaryRepository.Params): SummaryDto {
        val isEmpty = model.grandTotal.totalSeconds == 0
        return SummaryDto(model.range.date, false, isEmpty, emptyList())
    }

}