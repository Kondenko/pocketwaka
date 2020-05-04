package com.kondenko.pocketwaka.utils

import com.kondenko.pocketwaka.BuildConfig
import com.kondenko.pocketwaka.utils.extensions.report
import timber.log.Timber

@Suppress("NOTHING_TO_INLINE") // inlining is required to allow Timber to use class names as tags
object WakaLog {

    inline fun d(message: String) {
        Timber.d(message)
        ifNoTimber {
            println(message)
        }
    }

    inline fun v(message: String) {
        Timber.v(message)
        ifNoTimber {
            println(message)
        }
    }

    inline fun w(message: String, throwable: Throwable? = null) {
        throwable?.report(message, printLog = false)
        Timber.w(throwable, message)
        ifNoTimber {
            println(message)
            throwable?.let {
                println("Exception:")
                it.printStackTrace()
            }
        }
    }

    inline fun e(message: String?, throwable: Throwable? = null) {
        Timber.e(throwable, message)
        ifNoTimber {
            println(message)
            throwable?.let {
                println("Exception:")
                it.printStackTrace()
            }
        }
    }

    inline fun e(throwable: Throwable? = null) {
        throwable?.report(printLog = false)
        Timber.e(throwable)
        ifNoTimber {
            throwable?.let {
                println("Exception:")
                it.printStackTrace()
            }
        }
    }

    inline fun w(throwable: Throwable? = null) {
        throwable?.report(printLog = false)
        Timber.w(throwable)
        ifNoTimber {
            throwable?.let {
                println("Exception:")
                it.printStackTrace()
            }
        }
    }

    inline fun ifNoTimber(action: () -> Unit) {
        if (Timber.treeCount() == 0 && BuildConfig.DEBUG) action()
    }

}