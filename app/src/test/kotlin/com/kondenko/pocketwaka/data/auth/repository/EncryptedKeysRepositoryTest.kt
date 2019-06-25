package com.kondenko.pocketwaka.data.auth.repository

import com.kondenko.pocketwaka.testutils.RxRule
import com.nhaarman.mockito_kotlin.mock
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class EncryptedKeysRepositoryTest {

    @get:Rule
    val rxRule = RxRule()

    val repository = EncryptedKeysRepository()

    @Test(expected = UnsatisfiedLinkError::class)
    fun `should get app id`() {
        with(repository.appId.test()) {
            assertNoErrors()
            assertComplete()
            assertValue(mock<String>())
        }
    }

    @Test(expected = UnsatisfiedLinkError::class)
    fun `should get app secret`() {
        with(repository.appSecret.test()) {
            assertNoErrors()
            assertComplete()
            assertValue(mock<String>())
        }
    }

}