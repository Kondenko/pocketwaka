package com.kondenko.pocketwaka.utils.extensions

import io.reactivex.Observable
import io.reactivex.exceptions.CompositeException
import io.reactivex.observers.TestObserver

fun <T> Observable<T>.testWithLogging(): TestObserver<T> = this
        .doOnEach {
            println(it)
            (it.error as? CompositeException)?.let {
                println("Exceptions: ")
                it.exceptions.forEach(::println)
            }
        }
        .test()