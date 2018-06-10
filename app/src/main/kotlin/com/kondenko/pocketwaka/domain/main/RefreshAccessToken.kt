package com.kondenko.pocketwaka.domain.main

import com.kondenko.pocketwaka.Const
import com.kondenko.pocketwaka.data.auth.model.AccessToken
import com.kondenko.pocketwaka.data.auth.repository.AccessTokenRepository
import com.kondenko.pocketwaka.domain.UseCaseSingle
import com.kondenko.pocketwaka.domain.auth.GetAppId
import com.kondenko.pocketwaka.domain.auth.GetAppSecret
import com.kondenko.pocketwaka.utils.Encryptor
import com.kondenko.pocketwaka.utils.SchedulerContainer
import com.kondenko.pocketwaka.utils.currentTimeSec
import io.reactivex.Single
import io.reactivex.rxkotlin.zipWith
import timber.log.Timber
import javax.inject.Inject

/**
 * Refreshes AccessToken if the old one is expired.
 */
class RefreshAccessToken
@Inject constructor(
        schedulers: SchedulerContainer,
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
                    if (!token.isValid()) {
                        Timber.i("Token has expired, updating")
                        getAppId.build().zipWith(getAppSecret.build()) { id, secret ->
                            accessTokenRepository.getRefreshToken().flatMap { refreshToken ->
                                accessTokenRepository.getRefreshToken(id, secret, Const.AUTH_REDIRECT_URI, GRANT_TYPE_REFRESH_TOKEN, refreshToken)
                            }
                        }
                                .flatMap { it }
                                .map { encryptor.encryptToken(it) }
                                .doOnSuccess { accessTokenRepository.saveToken(it, currentTimeSec()) }
                    } else {
                        Timber.i("Token is valid, proceeding")
                        Single.just(token)
                    }
                }

    }

}