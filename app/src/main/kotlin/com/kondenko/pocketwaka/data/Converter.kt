package com.kondenko.pocketwaka.data

interface ModelConverter<P, T, R> {
    fun convert(model: T, param: P): R
}