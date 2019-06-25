package com.kondenko.pocketwaka.domain.auth

import com.kondenko.pocketwaka.data.auth.repository.EncryptedKeysRepository
import com.kondenko.pocketwaka.testutils.testSchedulers
import com.kondenko.pocketwaka.utils.encryption.Encryptor
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Single
import org.junit.Test

class GetAppIdTest {

   private val encryptedKeysRepository: EncryptedKeysRepository = mock()

   private val tokenEncryptor: Encryptor<String> = mock()

   private val useCase = GetAppId(testSchedulers, encryptedKeysRepository, tokenEncryptor)

    @Test
    fun `should execute without errors`() {
        val idEncrypted = "foo"
        val idDecrypted = "bar"
        whenever(encryptedKeysRepository.appId).doReturn(Single.just(idEncrypted))
        whenever(tokenEncryptor.decrypt(idEncrypted)).doReturn(idDecrypted)
        val single = useCase.invoke()
        verify(encryptedKeysRepository, times(1)).appId
        verify(tokenEncryptor, times(1)).decrypt(idEncrypted)
        val testObserver = single.test()
        with(testObserver) {
            assertSubscribed()
            assertNoErrors()
            assertComplete()
            assertValue(idDecrypted)
        }
    }

}