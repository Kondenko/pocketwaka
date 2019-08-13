package com.kondenko.pocketwaka.data

interface CacheableModel<out T> {
    val data: T
    val isFromCache: Boolean
    val isEmpty: Boolean
}