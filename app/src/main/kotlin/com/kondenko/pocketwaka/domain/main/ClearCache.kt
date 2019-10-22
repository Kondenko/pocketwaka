package com.kondenko.pocketwaka.domain.main

import com.kondenko.pocketwaka.data.auth.repository.AccessTokenRepository
import com.kondenko.pocketwaka.data.stats.dao.StatsDao
import com.kondenko.pocketwaka.data.summary.dao.SummaryDao
import com.kondenko.pocketwaka.domain.UseCaseCompletable
import com.kondenko.pocketwaka.utils.SchedulersContainer
import io.reactivex.Completable

/**
 * Clears user's cache
 */
class ClearCache(
        schedulers: SchedulersContainer,
        private val tokenRepository: AccessTokenRepository,
        statsDao: StatsDao,
        summaryDao: SummaryDao
) : UseCaseCompletable<Nothing>(schedulers) {

    private val clearCacheCompletables = Completable.mergeArray(
            statsDao.clearCache(),
            summaryDao.clearCache()
    )

    override fun build(params: Nothing?) =
               tokenRepository.deleteToken().mergeWith(clearCacheCompletables)

}