package com.kondenko.pocketwaka.utils

import android.util.Base64
import com.kondenko.pocketwaka.api.KeysManager

object Encryptor {
    fun decrypt(value: String) = String(Base64.decode(value, Base64.DEFAULT))
    fun encrypt(value: String) = String(Base64.encode(value.toByteArray(), Base64.DEFAULT))
}