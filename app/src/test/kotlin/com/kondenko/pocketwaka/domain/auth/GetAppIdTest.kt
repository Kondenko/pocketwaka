package com.kondenko.pocketwaka.domain.auth

import com.kondenko.pocketwaka.data.auth.repository.EncryptedKeysRepository
import com.kondenko.pocketwaka.testutils.testSchedulers
import com.kondenko.pocketwaka.utils.Encryptor
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Single
import org.junit.Test

class GetAppIdTest {

   private val encryptedKeysRepository: EncryptedKeysRepository = mock()

   private val encryptor: Encryptor = mock()

   private val useCase = GetAppId(testSchedulers, encryptedKeysRepository, encryptor)

    @Test
    fun `should execute without errors`() {
        val idEncrypted = "foo"
        val idDecrypted = "bar"
        whenever(encryptedKeysRepository.appId).doReturn(Single.just(idEncrypted))
        whenever(encryptor.decrypt(idEncrypted)).doReturn(idDecrypted)
        val single = useCase.execute()
        verify(encryptedKeysRepository, times(1)).appId
        verify(encryptor, times(1)).decrypt(idEncrypted)
        val testObserver = single.test()
        with(testObserver) {
            assertSubscribed()
            assertNoErrors()
            assertComplete()
            assertValue(idDecrypted)
        }
    }

}