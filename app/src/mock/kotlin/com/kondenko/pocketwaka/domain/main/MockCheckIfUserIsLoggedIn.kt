package com.kondenko.pocketwaka.domain.main

import com.kondenko.pocketwaka.domain.UseCaseSingle
import com.kondenko.pocketwaka.utils.SchedulersContainer
import io.reactivex.Single

/**
 * Checks whether user should log in to use the app.
 */

class MockCheckIfUserIsLoggedIn(schedulers: SchedulersContainer) : UseCaseSingle<Nothing, Boolean>(schedulers) {

    override fun build(params: Nothing?): Single<Boolean> =
          Single.just(true)

}