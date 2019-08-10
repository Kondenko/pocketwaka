package com.kondenko.pocketwaka.domain.main

import com.kondenko.pocketwaka.data.auth.repository.AccessTokenRepository
import com.kondenko.pocketwaka.data.ranges.dao.StatsDao
import com.kondenko.pocketwaka.domain.UseCaseCompletable
import com.kondenko.pocketwaka.utils.SchedulersContainer

/**
 * Clears user's cache
 */
class ClearCache(
        schedulers: SchedulersContainer,
        private val tokenRepository: AccessTokenRepository,
        private val statsDao: StatsDao
) : UseCaseCompletable<Nothing>(schedulers) {

    override fun build(params: Nothing?) = tokenRepository.deleteToken().mergeWith(statsDao.clearCache())

}