package com.kondenko.pocketwaka.domain.main

import com.kondenko.pocketwaka.data.auth.repository.AccessTokenRepository
import com.kondenko.pocketwaka.domain.UseCaseCompletable
import com.kondenko.pocketwaka.utils.SchedulerContainer

/**
 * Deletes the Access Token from the database.
 */
class DeleteSavedToken
(schedulers: SchedulerContainer, private val repository: AccessTokenRepository) : UseCaseCompletable<Nothing>(schedulers) {

    override fun build(params: Nothing?) = repository.deleteToken()

}