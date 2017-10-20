package com.kondenko.pocketwaka.domain.auth

import com.kondenko.pocketwaka.Const
import com.kondenko.pocketwaka.dagger.PerApp
import com.kondenko.pocketwaka.domain.UseCaseSingle
import com.kondenko.pocketwaka.utils.SchedulerContainer
import io.reactivex.Single
import javax.inject.Inject

/**
 * Fetches access token.
 */
@PerApp
class GetAuthUrl
@Inject constructor(schedulers: SchedulerContainer, private val getAppId: GetAppId)
    : UseCaseSingle<Nothing?, String>(schedulers) {

    override fun build(params: Nothing?): Single<String> {
        val scopes = arrayOf(Const.SCOPE_EMAIL, Const.SCOPE_READ_LOGGED_TIME, Const.SCOPE_READ_STATS, Const.SCOPE_READ_TEAMS)
        return getAppId.build()
                .map { id ->
                    Const.URL_AUTH + "?client_id=$id" +
                    "&response_type=${Const.RESPONSE_TYPE_CODE}" +
                    "&redirect_uri=${Const.AUTH_REDIRECT_URI}" +
                    "&scope=${scopes.joinToString(",")}" 
                }
    }

}