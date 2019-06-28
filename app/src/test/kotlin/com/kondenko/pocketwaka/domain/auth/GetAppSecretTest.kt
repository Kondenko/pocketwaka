package com.kondenko.pocketwaka.domain.auth

import com.kondenko.pocketwaka.data.auth.repository.EncryptedKeysRepository
import com.kondenko.pocketwaka.testutils.testSchedulers
import com.kondenko.pocketwaka.utils.encryption.Encryptor
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Single
import org.junit.Test

class GetAppSecretTest{

    private val encryptedKeysRepository: EncryptedKeysRepository = mock()

    private val tokenEncryptor: Encryptor<String> = mock()

    private val useCase = GetAppSecret(testSchedulers, encryptedKeysRepository, tokenEncryptor)

    @Test
    fun `should execute without errors`() {
        val secretEncrypted = "foo"
        val secretDecrypted = "bar"
        whenever(encryptedKeysRepository.appSecret).doReturn(Single.just(secretEncrypted))
        whenever(tokenEncryptor.decrypt(secretEncrypted)).doReturn(secretDecrypted)
        val single = useCase.invoke()
        verify(encryptedKeysRepository, times(1)).appSecret
        verify(tokenEncryptor, times(1)).decrypt(secretEncrypted)
        with(single.test()) {
            assertSubscribed()
            assertNoErrors()
            assertComplete()
            assertValue(secretDecrypted)
        }
    }

}