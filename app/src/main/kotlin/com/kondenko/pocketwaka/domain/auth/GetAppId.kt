package com.kondenko.pocketwaka.domain.auth

import com.kondenko.pocketwaka.dagger.PerScreen
import com.kondenko.pocketwaka.data.auth.repository.EncryptedKeysRepository
import com.kondenko.pocketwaka.domain.UseCaseSingle
import com.kondenko.pocketwaka.utils.Encryptor
import com.kondenko.pocketwaka.utils.SchedulerContainer
import io.reactivex.Single
import javax.inject.Inject

/**
 * Fetches app id from the database and decrypts it.
 */
@PerScreen
class GetAppId
@Inject constructor(
        schedulers: SchedulerContainer,
        private val encryptedKeysRepository: EncryptedKeysRepository,
        private val encryptor: Encryptor)
    : UseCaseSingle<Nothing, String>(schedulers) {

    override fun build(params: Nothing?): Single<String> = encryptedKeysRepository.appId.map { encryptor.decrypt(it) }

}