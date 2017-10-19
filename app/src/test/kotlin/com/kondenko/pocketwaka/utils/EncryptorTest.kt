package com.kondenko.pocketwaka.utils

import com.kondenko.pocketwaka.data.auth.model.AccessToken
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/**
 * Created by Kondenko on 10.10.2017.
 */
@RunWith(JUnit4::class)
class EncryptorTest {

    private val encryptor = Encryptor()
    private val token = AccessToken("a", 1.0, "b", "c", "d", "e", 0f)
    private val encryptedToken = encryptor.encryptToken(token)
    private val decryptedToken = encryptor.decryptToken(encryptedToken)

    @Test
    fun shouldBeEqual() = Assert.assertEquals(token, decryptedToken)

    @Test
    fun shouldDiffer() = Assert.assertNotEquals(token, encryptedToken)

}