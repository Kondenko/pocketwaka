package com.kondenko.pocketwaka.utils.types

class KOptional<T>(val item: T?) {

    companion object {
        fun <T> of(item: T) = KOptional(item)
        fun <T> ofNullable(item: T?) = KOptional(item)
        fun <T> empty() = KOptional<T>(null)
    }

    fun get(): T = item ?: throw NullPointerException("This KOptional is empty")

    fun isEmpty() = item == null

    fun isNotEmpty() = item != null

    fun orElse(defaultItem: T): T = item ?: defaultItem

}