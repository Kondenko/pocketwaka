package com.kondenko.pocketwaka.domain.main

import com.kondenko.pocketwaka.data.auth.model.AccessToken
import com.kondenko.pocketwaka.data.auth.repository.AccessTokenRepository
import com.kondenko.pocketwaka.domain.UseCaseSingle
import com.kondenko.pocketwaka.utils.Encryptor
import com.kondenko.pocketwaka.utils.SchedulersContainer
import io.reactivex.Single

/**
 * Fetches access token.
 */

class GetStoredAccessToken
(
        schedulers: SchedulersContainer,
        private val accessTokenRepository: AccessTokenRepository,
        private val encryptor: Encryptor
) : UseCaseSingle<Nothing, AccessToken>(schedulers) {

    override fun build(params: Nothing?): Single<AccessToken> {
        return accessTokenRepository.getEncryptedToken()
                .map { encryptor.decryptToken(it) }
    }

}