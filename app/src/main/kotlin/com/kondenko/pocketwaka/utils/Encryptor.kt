package com.kondenko.pocketwaka.utils

import android.util.Base64

object Encryptor {

    @JvmStatic
    fun decrypt(value: String) = String(Base64.decode(value, Base64.DEFAULT))

    @JvmStatic
    fun encrypt(value: String) = String(Base64.encode(value.toByteArray(), Base64.DEFAULT))

}