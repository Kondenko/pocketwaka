package com.kondenko.pocketwaka.utils.extensions

import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables

/**
 * An equivalent for calling [Observables.combineLatest] followed by [io.reactivex.Observable#flatMap]
 */
inline fun <T1 : Any, T2 : Any, R : Any> flatMapLatest(
        observable1: Observable<T1>,
        observable2: Observable<T2>,
        crossinline function: (T1, T2) -> Observable<out R>
): Observable<R> =
        Observables.combineLatest(observable1, observable2) { t1, t2 -> t1 to t2 }
                .flatMap { (t1, t2) -> function(t1, t2) }


fun <T> Observable<T>.testWithLogging() = this.doOnEach(::println).test()