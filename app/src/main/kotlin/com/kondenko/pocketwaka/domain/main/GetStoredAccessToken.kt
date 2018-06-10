package com.kondenko.pocketwaka.domain.main

import com.kondenko.pocketwaka.dagger.PerApp
import com.kondenko.pocketwaka.data.auth.model.AccessToken
import com.kondenko.pocketwaka.data.auth.repository.AccessTokenRepository
import com.kondenko.pocketwaka.domain.UseCaseSingle
import com.kondenko.pocketwaka.utils.Encryptor
import com.kondenko.pocketwaka.utils.SchedulerContainer
import io.reactivex.Single
import javax.inject.Inject

/**
 * Fetches access token.
 */
@PerApp
class GetStoredAccessToken
@Inject constructor(
        schedulers: SchedulerContainer,
        private val accessTokenRepository: AccessTokenRepository,
        private val encryptor: Encryptor
) : UseCaseSingle<String, AccessToken>(schedulers) {

    override fun build(code: String?): Single<AccessToken> {
        return accessTokenRepository.getEncryptedToken()
                .map { encryptor.decryptToken(it) }
    }

}