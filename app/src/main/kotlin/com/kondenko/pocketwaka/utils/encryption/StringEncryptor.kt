package com.kondenko.pocketwaka.utils.encryption

import android.util.Base64

class StringEncryptor : Encryptor<String> {

    override fun encrypt(value: String): String = Base64.encodeToString(value.toByteArray(), Base64.DEFAULT)

    override fun decrypt(value: String): String = String(Base64.decode(value, Base64.DEFAULT))

}