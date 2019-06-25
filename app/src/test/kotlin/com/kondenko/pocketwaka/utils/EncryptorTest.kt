package com.kondenko.pocketwaka.utils

import com.kondenko.pocketwaka.data.auth.model.AccessToken
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class EncryptorTest {

    private val encryptor = Encryptor()

    private val token = AccessToken("a", 1.0, "b", "c", "d", "e", 0f)

    @Test
    fun shouldEncryptCorrectly() {
        val encryptedToken = encryptor.encryptToken(token)
        assertNotEquals(token, encryptedToken)
    }

    @Test
    fun shouldDecryptCorrectly() {
        val encryptedToken = encryptor.encryptToken(token)
        val decryptedToken = encryptor.decryptToken(encryptedToken)
        assertNotEquals(encryptedToken, decryptedToken)
        assertEquals(token, decryptedToken)
    }

}