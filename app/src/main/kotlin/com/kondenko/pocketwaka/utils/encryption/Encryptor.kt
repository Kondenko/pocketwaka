package com.kondenko.pocketwaka.utils.encryption

interface Encryptor<T> {

    fun encrypt(value: T): T

    fun decrypt(value: T): T

}