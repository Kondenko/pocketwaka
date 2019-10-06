package com.kondenko.pocketwaka.utils

import timber.log.Timber

@Suppress("NOTHING_TO_INLINE") // inlining is required to allow Timber to use class names as tags
object WakaLog {

    inline fun d(message: String) {
        Timber.d(message)
        ifNoTimber {
            println(message)
        }
    }

    inline fun w(message: String, throwable: Throwable? = null) {
        Timber.w(throwable, message)
        ifNoTimber {
            println(message)
            throwable?.let {
                println("Exception:")
                it.printStackTrace()
            }
        }
    }

    inline fun w(throwable: Throwable? = null) {
        Timber.w(throwable)
        ifNoTimber {
            throwable?.let {
                println("Exception:")
                it.printStackTrace()
            }
        }
    }

    inline fun ifNoTimber(action: () -> Unit) {
        if (Timber.treeCount() == 0) action()
    }

}