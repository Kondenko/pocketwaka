package com.kondenko.pocketwaka.data.ranges.repository

import com.kondenko.pocketwaka.data.ModelConverter
import com.kondenko.pocketwaka.data.Repository
import com.kondenko.pocketwaka.data.ranges.dao.StatsDao
import com.kondenko.pocketwaka.data.ranges.model.database.StatsDbModel
import com.kondenko.pocketwaka.data.ranges.model.server.StatsServerModel
import com.kondenko.pocketwaka.data.ranges.service.StatsService
import io.reactivex.Completable

class StatsRepository(
        private val service: StatsService,
        private val dao: StatsDao,
        serverModelConverter: ModelConverter<Params, StatsServerModel, StatsDbModel?>,
        dbModelConverter: ModelConverter<Nothing?, StatsDbModel, StatsDbModel> // Dto now serves as a domain model as well but will be refactored later
) : Repository<StatsRepository.Params, StatsServerModel, StatsDbModel>(
        serverDataProvider = { (tokenHeader, range) -> service.getCurrentUserStats(tokenHeader, range) },
        cachedDataProvider = { dao.getCachedStats(it.range) },
        serviceResponseConverter = serverModelConverter
) {

    data class Params(val tokenHeader: String, val range: String)

    override fun cacheData(data: StatsDbModel): Completable = dao.cacheStats(data)

    override fun setIsFromCache(model: StatsDbModel, isFromCache: Boolean): StatsDbModel = model.copy(isFromCache = isFromCache)

}