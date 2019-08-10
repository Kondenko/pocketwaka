package com.kondenko.pocketwaka.data

interface Converter<T, R> {
    fun convert(param: T): R
}