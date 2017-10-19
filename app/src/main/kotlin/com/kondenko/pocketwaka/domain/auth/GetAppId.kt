package com.kondenko.pocketwaka.domain.auth

import com.kondenko.pocketwaka.dagger.PerApp
import com.kondenko.pocketwaka.data.auth.repository.EncryptedKeysRepository
import com.kondenko.pocketwaka.domain.UseCaseSingle
import com.kondenko.pocketwaka.utils.Encryptor
import com.kondenko.pocketwaka.utils.SchedulerContainer
import io.reactivex.Single
import javax.inject.Inject

/**
 * Fetches app id from the database and decrypts it.
 */
@PerApp
class GetAppId
@Inject constructor(schedulers: SchedulerContainer, private val encryptedKeysRepository: EncryptedKeysRepository, private val encryptor: Encryptor)
    : UseCaseSingle<Unit?, String>(schedulers) {

    override fun build(params: Unit?): Single<String> = encryptedKeysRepository.appId.map { encryptor.decrypt(it) }

}