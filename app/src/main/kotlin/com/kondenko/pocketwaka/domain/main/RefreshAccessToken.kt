package com.kondenko.pocketwaka.domain.main

import com.kondenko.pocketwaka.Const
import com.kondenko.pocketwaka.data.auth.model.AccessToken
import com.kondenko.pocketwaka.data.auth.repository.AccessTokenRepository
import com.kondenko.pocketwaka.domain.UseCaseSingle
import com.kondenko.pocketwaka.domain.auth.GetAppId
import com.kondenko.pocketwaka.domain.auth.GetAppSecret
import com.kondenko.pocketwaka.utils.Encryptor
import com.kondenko.pocketwaka.utils.SchedulerContainer
import com.kondenko.pocketwaka.utils.TimeProvider
import io.reactivex.Single
import io.reactivex.rxkotlin.Singles
import timber.log.Timber

/**
 * Refreshes AccessToken if the old one is expired.
 */

class RefreshAccessToken(
        schedulers: SchedulerContainer,
        private val timeProvider: TimeProvider,
        private val encryptor: Encryptor,
        private val accessTokenRepository: AccessTokenRepository,
        private val getStoredAccessToken: GetStoredAccessToken,
        private val getAppId: GetAppId,
        private val getAppSecret: GetAppSecret
) : UseCaseSingle<Nothing, AccessToken>(schedulers) {

    private val GRANT_TYPE_REFRESH_TOKEN = "refresh_token"

    override fun build(params: Nothing?): Single<AccessToken> {
        return getStoredAccessToken.build()
                .flatMap { token ->
                    if (!token.isValid(timeProvider.getCurrentTimeSec())) {
                        Timber.i("Token has expired, updating with ref_token = ${token.refreshToken}")
                        Singles.zip(getAppId.build(), getAppSecret.build()) { id, secret ->
                            accessTokenRepository.getRefreshedAccessToken(
                                    id,
                                    secret,
                                    Const.AUTH_REDIRECT_URI,
                                    GRANT_TYPE_REFRESH_TOKEN,
                                    token.refreshToken
                            )
                        }
                                .flatMap { it }
                                .doOnSuccess { Timber.i("Saved access token: $it") }
                                .map { encryptor.encryptToken(it) }
                                .doOnSuccess { accessTokenRepository.saveToken(it, timeProvider.getCurrentTimeSec()) }
                    } else {
                        Timber.i("Token is valid, proceeding")
                        Single.just(token)
                    }
                }
    }

}