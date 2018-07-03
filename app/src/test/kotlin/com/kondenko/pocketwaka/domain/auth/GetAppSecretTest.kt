package com.kondenko.pocketwaka.domain.auth

import com.kondenko.pocketwaka.data.auth.repository.EncryptedKeysRepository
import com.kondenko.pocketwaka.testutils.testSchedulers
import com.kondenko.pocketwaka.utils.Encryptor
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Single
import org.junit.Test

class GetAppSecretTest{

    private val encryptedKeysRepository: EncryptedKeysRepository = mock()

    private val encryptor: Encryptor = mock()

    private val useCase = GetAppSecret(testSchedulers, encryptedKeysRepository, encryptor)

    @Test
    fun `should execute without errors`() {
        val secretEncrypted = "foo"
        val secretDecrypted = "bar"
        whenever(encryptedKeysRepository.appSecret).doReturn(Single.just(secretEncrypted))
        whenever(encryptor.decrypt(secretEncrypted)).doReturn(secretDecrypted)
        val single = useCase.execute()
        verify(encryptedKeysRepository, times(1)).appSecret
        verify(encryptor, times(1)).decrypt(secretEncrypted)
        with(single.test()) {
            assertSubscribed()
            assertNoErrors()
            assertComplete()
            assertValue(secretDecrypted)
        }
    }

}