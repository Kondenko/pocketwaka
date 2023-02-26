package com.kondenko.pocketwaka.domain.auth

import com.kondenko.pocketwaka.Const
import com.kondenko.pocketwaka.data.auth.model.server.AccessToken
import com.kondenko.pocketwaka.data.auth.repository.AccessTokenRepository
import com.kondenko.pocketwaka.domain.UseCaseSingle
import com.kondenko.pocketwaka.utils.SchedulersContainer
import com.kondenko.pocketwaka.utils.encryption.Encryptor
import io.reactivex.rxkotlin.Singles

private const val GRANT_TYPE_AUTH_CODE = "authorization_code"

/**
 * Fetches access token.
 */
class GetAccessToken(
    schedulers: SchedulersContainer,
    private val tokenEncryptor: Encryptor<AccessToken>,
    private val accessTokenRepository: AccessTokenRepository,
    private val getAppId: GetAppId,
    private val getAppSecret: GetAppSecret
) : UseCaseSingle<String, AccessToken>(schedulers) {

    override fun build(code: String?) =
        Singles.zip(getAppId.build(), getAppSecret.build()) { id: String, secret: String ->
            accessTokenRepository.getNewAccessToken(
                id, secret, Const.AUTH_REDIRECT_URI, GRANT_TYPE_AUTH_CODE, code!!
            )
        }
            .flatMap { it }
            .map { tokenEncryptor.encrypt(it) }
            .doOnSuccess(accessTokenRepository::saveToken)

}