package com.kondenko.pocketwaka.data.daily.repository

import com.kondenko.pocketwaka.data.ModelConverter
import com.kondenko.pocketwaka.data.Repository
import com.kondenko.pocketwaka.data.daily.dto.SummaryRangeDto
import com.kondenko.pocketwaka.data.daily.model.Summary
import com.kondenko.pocketwaka.data.daily.service.SummaryService
import com.kondenko.pocketwaka.utils.date.DateRangeString
import io.reactivex.Completable
import io.reactivex.Maybe

class SummaryRepository(
        summaryService: SummaryService,
        summaryResponseConverter: ModelConverter<Params, Summary, SummaryRangeDto?>
) : Repository<SummaryRepository.Params, Summary, SummaryRangeDto>(
        serverDataProvider = { (range, project, branches): Params ->
            summaryService.getSummaries(range.start, range.end, project, branches)
        },
        cachedDataProvider = { Maybe.empty() }, // TODO Retrieve data from cache
        serviceResponseConverter = summaryResponseConverter
) {

    data class Params(
            val dateRangeString: DateRangeString,
            val project: String? = null,
            val branches: String? = null
    )

    /**
     * TODO Implement caching
     */
    override fun cacheData(data: SummaryRangeDto): Completable = Completable.error(NotImplementedError())

    override fun setIsFromCache(model: SummaryRangeDto, isFromCache: Boolean): SummaryRangeDto = model.copy(isFromCache = isFromCache)

}