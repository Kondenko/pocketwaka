package com.kondenko.pocketwaka.domain.auth

import com.kondenko.pocketwaka.data.auth.repository.EncryptedKeysRepository
import com.kondenko.pocketwaka.domain.UseCaseSingle
import com.kondenko.pocketwaka.utils.Encryptor
import com.kondenko.pocketwaka.utils.SchedulerContainer
import io.reactivex.Single

/**
 * Fetches app id from the database and decrypts it.
 */

class GetAppId
(
        schedulers: SchedulerContainer,
        private val encryptedKeysRepository: EncryptedKeysRepository,
        private val encryptor: Encryptor
) : UseCaseSingle<Nothing, String>(schedulers) {

    override fun build(params: Nothing?): Single<String> = encryptedKeysRepository.appId.map { encryptor.decrypt(it) }

}