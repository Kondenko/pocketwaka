package com.kondenko.pocketwaka.data.summary.repository

import com.kondenko.pocketwaka.data.Repository
import com.kondenko.pocketwaka.data.summary.model.database.SummaryRangeDto
import com.kondenko.pocketwaka.data.summary.model.server.Summary
import com.kondenko.pocketwaka.data.summary.service.SummaryService
import io.reactivex.Completable
import io.reactivex.Maybe

class SummaryRepository(summaryService: SummaryService) :
        Repository<SummaryRepository.Params, Summary, SummaryRangeDto>(
                serverDataProvider = { (tokenHeader, start, end, project, branches): Params ->
                    summaryService.getSummaries(tokenHeader, start, end, project, branches)
                },
                cachedDataProvider = { Maybe.empty() } // Implement cache retrieval
        ) {

    data class Params(
            val tokenHeader: String,
            val start: String,
            val end: String,
            val project: String? = null,
            val branches: String? = null
    )

    override fun cacheData(data: SummaryRangeDto): Completable = Completable.complete() // Implement caching

    override fun setIsFromCache(model: SummaryRangeDto, isFromCache: Boolean): SummaryRangeDto = model.copy(isFromCache = isFromCache)

}