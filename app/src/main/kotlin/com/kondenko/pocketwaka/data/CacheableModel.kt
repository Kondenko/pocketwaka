package com.kondenko.pocketwaka.data

interface CacheableModel<T> {
    val data: T
    val isFromCache: Boolean
    val isEmpty: Boolean
}