package com.kondenko.pocketwaka.utils

import com.kondenko.pocketwaka.CustomRobolectricTestRunner
import com.kondenko.pocketwaka.data.auth.model.AccessToken
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(CustomRobolectricTestRunner::class)
class EncryptorTest {

    private val encryptor = Encryptor()

    private val token = AccessToken("a", 1.0, "b", "c", "d", "e", 0f)
    private val encryptedToken = encryptor.encryptToken(token)
    private val decryptedToken = encryptor.decryptToken(encryptedToken)

    @Test
    fun shouldEncryptCorrectly() = assertEquals(token, decryptedToken)

    @Test
    fun shouldDecryptCorrectly() = assertNotEquals(token, encryptedToken)

}