package com.kondenko.pocketwaka.domain.auth

import com.kondenko.pocketwaka.BuildConfig
import com.kondenko.pocketwaka.domain.UseCaseSingle
import com.kondenko.pocketwaka.utils.SchedulersContainer
import io.reactivex.Single
import java.util.*
import java.util.Calendar.*

class MockGetTokenHeaderValue(
      schedulers: SchedulersContainer
) : UseCaseSingle<Nothing, String>(schedulers) {

    private val HEADER_BEARER_VALUE_PREFIX = "Bearer"

    override fun build(params: Nothing?): Single<String> =
          Single.just("$HEADER_BEARER_VALUE_PREFIX ${BuildConfig.ACCESS_TOKEN}")
          .doOnSuccess { crashIfOutdated() }

    private fun crashIfOutdated() {
        with(Calendar.getInstance()) {
            time = Date()
            if (!(get(YEAR) == 2019 && get(MONTH) == OCTOBER && get(DAY_OF_MONTH) in 16..20)) {
                throw RuntimeException("Mock access token has expired")
            }
        }
    }

}