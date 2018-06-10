package com.kondenko.pocketwaka.domain.auth

import com.kondenko.pocketwaka.dagger.PerScreen
import com.kondenko.pocketwaka.data.auth.repository.AccessTokenRepository
import com.kondenko.pocketwaka.domain.UseCaseSingle
import com.kondenko.pocketwaka.utils.Encryptor
import com.kondenko.pocketwaka.utils.SchedulerContainer
import javax.inject.Inject

@PerScreen
class GetTokenHeaderValue
@Inject constructor(
        schedulers: SchedulerContainer,
        private val encryptor: Encryptor,
        private val accessTokenRepository: AccessTokenRepository
) : UseCaseSingle<Nothing, String>(schedulers) {

    private val HEADER_BEARER_VALUE_PREFIX = "Bearer"

    override fun build(params: Nothing?)
            = accessTokenRepository.getEncryptedTokenValue()
            .map { encryptor.decrypt(it) }
            .map { "$HEADER_BEARER_VALUE_PREFIX $it"}

}