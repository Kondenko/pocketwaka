package com.kondenko.pocketwaka.domain.auth

import com.kondenko.pocketwaka.data.auth.repository.EncryptedKeysRepository
import com.kondenko.pocketwaka.domain.UseCaseSingle
import com.kondenko.pocketwaka.utils.SchedulersContainer
import com.kondenko.pocketwaka.utils.encryption.Encryptor
import io.reactivex.Single

/**
 * Fetches app id from the database and decrypts it.
 */

class GetAppId(
        schedulers: SchedulersContainer,
        private val encryptedKeysRepository: EncryptedKeysRepository,
        private val stringEncryptor: Encryptor<String>
) : UseCaseSingle<Nothing, String>(schedulers) {

    override fun build(params: Nothing?): Single<String> = encryptedKeysRepository.appId.map { stringEncryptor.decrypt(it) }

}