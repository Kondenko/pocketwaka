package com.kondenko.pocketwaka.domain.main

import com.kondenko.pocketwaka.dagger.PerScreen
import com.kondenko.pocketwaka.data.auth.repository.AccessTokenRepository
import com.kondenko.pocketwaka.domain.UseCaseSingle
import com.kondenko.pocketwaka.utils.SchedulerContainer
import io.reactivex.Single
import javax.inject.Inject

/**
 * Checks whether user should log in to use the app.
 */
@PerScreen
class CheckIfUserIsLoggedIn
@Inject constructor(schedulers: SchedulerContainer, private val repository: AccessTokenRepository) : UseCaseSingle<Nothing, Boolean>(schedulers) {

    override fun build(params: Nothing?): Single<Boolean> = repository.isTokenSaved()

}