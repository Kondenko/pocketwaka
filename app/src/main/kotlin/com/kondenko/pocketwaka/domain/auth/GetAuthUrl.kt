package com.kondenko.pocketwaka.domain.auth

import com.kondenko.pocketwaka.Const
import com.kondenko.pocketwaka.dagger.PerScreen
import com.kondenko.pocketwaka.domain.UseCaseSingle
import com.kondenko.pocketwaka.utils.SchedulerContainer
import io.reactivex.Single
import javax.inject.Inject

/**
 * Fetches access token.
 */
@PerScreen
class GetAuthUrl
@Inject constructor(schedulers: SchedulerContainer, private val getAppId: GetAppId) : UseCaseSingle<Nothing?, String>(schedulers) {

    private val urlAuth = "${Const.BASE_URL}oauth/authorize" // Used to authenticate a user

    private val responseType = "code"

    override fun build(params: Nothing?): Single<String> {
        val scopes = arrayOf("email", "read_logged_time", "read_stats", "read_teams").joinToString(",")
        return getAppId.build()
                .map { id -> "$urlAuth?client_id=$id&response_type=$responseType&redirect_uri=${Const.AUTH_REDIRECT_URI}&scope=$scopes&force_approve=true" }
                .map { it.trimMargin() }
    }

}