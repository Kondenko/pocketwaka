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
    : UseCaseSingle<Unit?, String>(schedulers) {

    override fun build(params: Unit?): Single<String> {
        val scopes = arrayOf(Const.SCOPE_EMAIL, Const.SCOPE_READ_LOGGED_TIME, Const.SCOPE_READ_STATS, Const.SCOPE_READ_TEAMS)
        return getAppId.build(null)
                .map { id ->
                    Const.URL_AUTH + "?client_id=$id" +
                    "&response_type=${Const.RESPONSE_TYPE_CODE}" +
                    "&redirect_uri=${Const.AUTH_REDIRECT_URI}" +
                    "&scope=${scopes.joinToString(",")}" 
                }
        // https://wakatime.com/oauth/authorize?client_id=b'BSdizeGtyagrFYG9rh5il9p1'&response_type=code&redirect_uri=pocketwaka://oauth2&scope=email,read_logged_time,read_stats,read_teams

    }

}