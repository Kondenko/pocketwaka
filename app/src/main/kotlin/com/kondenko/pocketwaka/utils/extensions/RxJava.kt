package com.kondenko.pocketwaka.utils.extensions

import io.reactivex.Observable
import io.reactivex.ObservableSource
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

fun <T, R> Observable<T>.concatMapEagerDelayError(mapper: (T) -> ObservableSource<out R>) =
        concatMapEagerDelayError(mapper, true)

fun <T> Observable<T>.startWithIfNotEmpty(item: T): Observable<T> {
    return this.isEmpty.flatMapObservable { isEmpty ->
        if (!isEmpty) this.startWith(item)
        else this
    }
}