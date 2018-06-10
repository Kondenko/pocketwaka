package com.kondenko.pocketwaka.domain.main

import com.kondenko.pocketwaka.data.auth.repository.AccessTokenRepository
import com.kondenko.pocketwaka.domain.UseCaseCompletable
import com.kondenko.pocketwaka.utils.SchedulerContainer
import javax.inject.Inject

/**
 * Deletes the Access Token from the database.
 */
class DeleteSavedToken
@Inject constructor(schedulers: SchedulerContainer, private val repository: AccessTokenRepository) : UseCaseCompletable<Nothing>(schedulers) {

    override fun build(params: Nothing?) = repository.deleteToken()

}