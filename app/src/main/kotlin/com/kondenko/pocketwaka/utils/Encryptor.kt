package com.kondenko.pocketwaka.utils

import android.util.Base64
import com.kondenko.pocketwaka.data.auth.model.AccessToken

class Encryptor {

    fun encryptToken(token: AccessToken) = AccessToken(
            access_token = encrypt(token.access_token),
            expires_in = token.expires_in,
            refresh_token = encrypt(token.refresh_token),
            scope = encrypt(token.scope),
            token_type = encrypt(token.token_type),
            uid = encrypt(token.uid),
            created_at = token.created_at
    )

    fun decryptToken(token: AccessToken) = AccessToken(
            access_token = decrypt(token.access_token),
            expires_in = token.expires_in,
            refresh_token = decrypt(token.refresh_token),
            scope = decrypt(token.scope),
            token_type = decrypt(token.token_type),
            uid = decrypt(token.uid),
            created_at = token.created_at
    )

    fun encrypt(value: String): String = Base64.encodeToString(value.toByteArray(), Base64.DEFAULT)

    fun decrypt(value: String): String = String(Base64.decode(value, Base64.DEFAULT))

}

