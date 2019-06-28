package com.kondenko.pocketwaka.domain.auth

import com.kondenko.pocketwaka.data.auth.repository.AccessTokenRepository
import com.kondenko.pocketwaka.domain.UseCaseSingle
import com.kondenko.pocketwaka.utils.SchedulersContainer
import com.kondenko.pocketwaka.utils.encryption.Encryptor
import io.reactivex.Single


class GetTokenHeaderValue(
        schedulers: SchedulersContainer,
        private val accessTokenRepository: AccessTokenRepository,
        private val stringEncryptor: Encryptor<String>
) : UseCaseSingle<Nothing, String>(schedulers) {

    private val HEADER_BEARER_VALUE_PREFIX = "Bearer"

    override fun build(params: Nothing?): Single<String>
            = accessTokenRepository.getEncryptedTokenValue()
            .map { stringEncryptor.decrypt(it) }
            .map { "$HEADER_BEARER_VALUE_PREFIX $it"}

}