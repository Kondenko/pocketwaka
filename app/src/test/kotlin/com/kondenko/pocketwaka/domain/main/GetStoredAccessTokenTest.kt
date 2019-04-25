package com.kondenko.pocketwaka.domain.main

import com.kondenko.pocketwaka.data.auth.model.AccessToken
import com.kondenko.pocketwaka.data.auth.repository.AccessTokenRepository
import com.kondenko.pocketwaka.testutils.testSchedulers
import com.kondenko.pocketwaka.utils.Encryptor
import com.nhaarman.mockito_kotlin.*
import io.reactivex.rxkotlin.toSingle
import org.junit.Test

class GetStoredAccessTokenTest {

    private val encryptor: Encryptor = mock()

    private val repository: AccessTokenRepository = mock()

    val usecase = GetStoredAccessToken(testSchedulers, repository, encryptor)

    @Test
    fun `should decrypt access token`() {
        val encryptedToken: AccessToken = mock()
        val decryptedToken: AccessToken = mock()
        whenever(repository.getEncryptedToken()).doReturn(encryptedToken.toSingle())
        whenever(encryptor.decryptToken(encryptedToken)).doReturn(decryptedToken)
        val single = usecase.execute()
        verify(repository).getEncryptedToken()
        verify(encryptor).decryptToken(eq(encryptedToken))
        with(single.test()) {
            assertSubscribed()
            assertNoErrors()
            assertComplete()
            assertValue(decryptedToken)
        }
    }

}