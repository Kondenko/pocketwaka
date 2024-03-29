package com.kondenko.pocketwaka.domain.main

import com.kondenko.pocketwaka.Const
import com.kondenko.pocketwaka.data.auth.model.server.AccessToken
import com.kondenko.pocketwaka.data.auth.repository.AccessTokenRepository
import com.kondenko.pocketwaka.domain.UseCaseSingle
import com.kondenko.pocketwaka.domain.auth.GetAppId
import com.kondenko.pocketwaka.domain.auth.GetAppSecret
import com.kondenko.pocketwaka.utils.SchedulersContainer
import com.kondenko.pocketwaka.utils.date.DateProvider
import com.kondenko.pocketwaka.utils.encryption.Encryptor
import io.reactivex.Single
import io.reactivex.rxkotlin.Singles
import timber.log.Timber

/**
 * Refreshes AccessToken if the old one is expired.
 */

class RefreshAccessToken(
    schedulers: SchedulersContainer,
    private val dateProvider: DateProvider,
    private val tokenEncryptor: Encryptor<AccessToken>,
    private val accessTokenRepository: AccessTokenRepository,
    private val getStoredAccessToken: GetStoredAccessToken,
    private val getAppId: GetAppId,
    private val getAppSecret: GetAppSecret
) : UseCaseSingle<Nothing, AccessToken>(schedulers) {

    private val GRANT_TYPE_REFRESH_TOKEN = "refreshToken"

    override fun build(params: Nothing?): Single<AccessToken> {
        return getStoredAccessToken.build()
            .flatMap { token ->
                if (!token.isValid(dateProvider.getCurrentTimeSec())) {
                    Timber.i("Token has expired, updating")
                    Singles.zip(getAppId.build(), getAppSecret.build()) { id, secret ->
                        accessTokenRepository.getRefreshToken()
                            .flatMap { refreshToken ->
                                accessTokenRepository.getRefreshedAccessToken(
                                    id,
                                    secret,
                                    Const.AUTH_REDIRECT_URI,
                                    GRANT_TYPE_REFRESH_TOKEN,
                                    refreshToken
                                )
                            }
                    }
                        .flatMap { it }
                        .doOnSuccess { newAccessToken ->
                            val encryptedToken = tokenEncryptor.encrypt(newAccessToken)
                            accessTokenRepository.saveToken(encryptedToken)
                        }
                } else {
                    Timber.i("Token is valid, proceeding")
                    Single.just(token)
                }
            }
    }

}