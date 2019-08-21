package com.kondenko.pocketwaka.data.summary.repository

import com.kondenko.pocketwaka.data.ModelConverter
import com.kondenko.pocketwaka.data.Repository
import com.kondenko.pocketwaka.data.summary.dto.SummaryRangeDto
import com.kondenko.pocketwaka.data.summary.model.Summary
import com.kondenko.pocketwaka.data.summary.service.SummaryService
import io.reactivex.Completable
import io.reactivex.Maybe

class SummaryRepository(
        summaryService: SummaryService,
        summaryResponseConverter: ModelConverter<Params, Summary, SummaryRangeDto?>
) : Repository<SummaryRepository.Params, Summary, SummaryRangeDto>(
        serverDataProvider = { (tokenHeader, start, end, project, branches): Params ->
            summaryService.getSummaries(tokenHeader, start, end, project, branches)
        },
        cachedDataProvider = { Maybe.error(NotImplementedError("Implement cache retrieval")) },
        serviceResponseConverter = summaryResponseConverter
) {

    data class Params(
            val tokenHeader: String,
            val start: String,
            val end: String,
            val project: String? = null,
            val branches: String? = null
    )

    /**
     * TODO Implement caching
     */
    override fun cacheData(data: SummaryRangeDto): Completable = Completable.error(NotImplementedError("Implement caching"))

    override fun setIsFromCache(model: SummaryRangeDto, isFromCache: Boolean): SummaryRangeDto = model.copy(isFromCache = isFromCache)

}