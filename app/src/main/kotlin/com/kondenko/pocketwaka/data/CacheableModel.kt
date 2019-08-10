package com.kondenko.pocketwaka.data

open class CacheableModel<T>(val data: T?, val isFromCache: Boolean = false)