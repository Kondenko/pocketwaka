package com.kondenko.pocketwaka.utils

class KOptional<T>(val item: T?) {

    companion object {
        fun <T> of(item: T) = KOptional(item)
        fun <T> ofNullable(item: T?) = KOptional(item)
        fun <T> empty() = KOptional<T>(null)
    }

    fun orElse(defaultItem: T): T = item ?: defaultItem

}