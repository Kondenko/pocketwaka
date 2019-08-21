package com.kondenko.pocketwaka.data.ranges.repository

import com.kondenko.pocketwaka.data.ModelConverter
import com.kondenko.pocketwaka.data.Repository
import com.kondenko.pocketwaka.data.ranges.dao.StatsDao
import com.kondenko.pocketwaka.data.ranges.dto.StatsDto
import com.kondenko.pocketwaka.data.ranges.model.StatsServiceResponse
import com.kondenko.pocketwaka.data.ranges.service.StatsService
import io.reactivex.Completable

class StatsRepository(
        private val service: StatsService,
        private val dao: StatsDao,
        serviceResponseConverter: ModelConverter<Params, StatsServiceResponse, StatsDto?>,
        dtoConverter: ModelConverter<Nothing?, StatsDto, StatsDto> // Dto now serves as a domain model as well but will be refactored later
) : Repository<StatsRepository.Params, StatsServiceResponse, StatsDto>(
        serverDataProvider = { (tokenHeader, range) -> service.getCurrentUserStats(tokenHeader, range) },
        cachedDataProvider = { dao.getCachedStats(it.range) },
        serviceResponseConverter = serviceResponseConverter
) {

    data class Params(val tokenHeader: String, val range: String)

    override fun cacheData(data: StatsDto): Completable = dao.cacheStats(data)

    override fun setIsFromCache(model: StatsDto, isFromCache: Boolean): StatsDto = model.copy(isFromCache = isFromCache)

}