package com.kondenko.pocketwaka.utils

import android.util.Base64
import com.kondenko.pocketwaka.data.auth.model.AccessToken
import java.nio.ByteBuffer

class Encryptor {

    fun encryptToken(token: AccessToken) = token.copy(
            access_token = encrypt(token.access_token),
            expires_in = encrypt(token.expires_in),
            refresh_token = encrypt(token.refresh_token),
            scope = encrypt(token.scope),
            token_type = encrypt(token.token_type),
            uid = encrypt(token.uid),
            created_at = encrypt(token.created_at)
    )

    fun decryptToken(token: AccessToken) = token.copy(
            access_token = decrypt(token.access_token),
            expires_in = decrypt(token.expires_in),
            refresh_token = decrypt(token.refresh_token),
            scope = decrypt(token.scope),
            token_type = decrypt(token.token_type),
            uid = decrypt(token.uid),
            created_at = decrypt(token.created_at)
    )

    fun encrypt(value: String) = String(Base64.encode(value.toByteArray(), Base64.DEFAULT))

    fun decrypt(value: String) = String(Base64.decode(value, Base64.DEFAULT))

    fun encrypt(value: Double) = ByteBuffer.wrap(Base64.encode(value.toByteArray(), Base64.DEFAULT)).double

    fun decrypt(value: Double) = ByteBuffer.wrap(Base64.decode(value.toByteArray(), Base64.DEFAULT)).double

    fun encrypt(value: Float) = ByteBuffer.wrap(Base64.encode(value.toByteArray(), Base64.DEFAULT)).float

    fun decrypt(value: Float) = ByteBuffer.wrap(Base64.decode(value.toByteArray(), Base64.DEFAULT)).float

    private fun Double.toByteArray() = ByteBuffer.allocate(8).putDouble(this).array()

    private fun Float.toByteArray() = ByteBuffer.allocate(8).putFloat(this).array()

}