package com.kondenko.pocketwaka.domain.auth

import com.kondenko.pocketwaka.BuildConfig
import com.kondenko.pocketwaka.domain.UseCaseSingle
import com.kondenko.pocketwaka.utils.SchedulersContainer
import io.reactivex.Single

class MockGetTokenHeaderValue(
      schedulers: SchedulersContainer
) : UseCaseSingle<Nothing, String>(schedulers) {

    private val HEADER_BEARER_VALUE_PREFIX = "Bearer"

    override fun build(params: Nothing?): Single<String> =
          Single.just("$HEADER_BEARER_VALUE_PREFIX ${BuildConfig.ACCESS_TOKEN}")

}