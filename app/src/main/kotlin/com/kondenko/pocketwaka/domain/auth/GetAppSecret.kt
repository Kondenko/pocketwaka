package com.kondenko.pocketwaka.domain.auth

import com.kondenko.pocketwaka.data.auth.repository.EncryptedKeysRepository
import com.kondenko.pocketwaka.domain.UseCaseSingle
import com.kondenko.pocketwaka.utils.Encryptor
import com.kondenko.pocketwaka.utils.SchedulersContainer
import io.reactivex.Single

/**
 * Fetches app secret from the database and decrypts it.
 */

class GetAppSecret(
        schedulers: SchedulersContainer,
        private val encryptedKeysRepository: EncryptedKeysRepository,
        private val encryptor: Encryptor
) : UseCaseSingle<Unit?, String>(schedulers) {

    override fun build(params: Unit?): Single<String> = encryptedKeysRepository.appSecret.map { encryptor.decrypt(it) }

}