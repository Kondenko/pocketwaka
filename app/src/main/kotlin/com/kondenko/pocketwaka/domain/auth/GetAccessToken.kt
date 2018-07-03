package com.kondenko.pocketwaka.domain.auth

import com.kondenko.pocketwaka.Const
import com.kondenko.pocketwaka.data.auth.model.AccessToken
import com.kondenko.pocketwaka.data.auth.repository.AccessTokenRepository
import com.kondenko.pocketwaka.domain.UseCaseSingle
import com.kondenko.pocketwaka.utils.Encryptor
import com.kondenko.pocketwaka.utils.SchedulerContainer
import com.kondenko.pocketwaka.utils.TimeProvider
import io.reactivex.Single
import io.reactivex.rxkotlin.zipWith

/**
 * Fetches access token.
 */
class GetAccessToken(
        schedulers: SchedulerContainer,
        private val timeProvider: TimeProvider,
        private val encryptor: Encryptor,
        private val accessTokenRepository: AccessTokenRepository,
        private val getAppId: GetAppId,
        private val getAppSecret: GetAppSecret
) : UseCaseSingle<String, AccessToken>(schedulers) {

    private val GRANT_TYPE_AUTH_CODE = "authorization_code"

    override fun build(code: String?): Single<AccessToken> {
        return getAppId.build().zipWith(getAppSecret.build())
        { id, secret -> accessTokenRepository.getNewAccessToken(id, secret, Const.AUTH_REDIRECT_URI, GRANT_TYPE_AUTH_CODE, code!!) }
                .flatMap { it }
                .map { encryptor.encryptToken(it) }
                .doOnSuccess { accessTokenRepository.saveToken(it, timeProvider.getCurrentTimeSec()) }
    }

}