package com.kondenko.pocketwaka.domain.main

import com.kondenko.pocketwaka.data.auth.model.AccessToken
import com.kondenko.pocketwaka.data.auth.repository.AccessTokenRepository
import com.kondenko.pocketwaka.testutils.testSchedulers
import com.kondenko.pocketwaka.utils.Encryptor
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.rxkotlin.toSingle
import org.junit.Test

class GetStoredAccessTokenTest {

    private val encryptor: Encryptor = mock()

    private val repository: AccessTokenRepository = mock()

    private val useCase = GetStoredAccessToken(testSchedulers, repository, encryptor)

    @Test
    fun `should decrypt access token`() {
        val encryptedToken: AccessToken = mock()
        val decryptedToken: AccessToken = mock()

        whenever(repository.getEncryptedToken()).doReturn(encryptedToken.toSingle())
        whenever(encryptor.decryptToken(encryptedToken)).doReturn(decryptedToken)

        val single = useCase.invoke()

        verify(repository).getEncryptedToken()
        verify(encryptor.decryptToken(encryptedToken))

        with(single.test()) {
            assertSubscribed()
            assertNoErrors()
            assertComplete()
            assertValue(decryptedToken)
        }
    }

}