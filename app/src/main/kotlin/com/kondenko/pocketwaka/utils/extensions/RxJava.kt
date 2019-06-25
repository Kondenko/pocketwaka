package com.kondenko.pocketwaka.utils.extensions

import io.reactivex.Observable
import io.reactivex.exceptions.CompositeException
import io.reactivex.observers.TestObserver
import io.reactivex.rxkotlin.Observables

fun <T> T.toObservable(): Observable<T> = Observable.just(this)

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

/**
 * An equivalent for calling [Observables.combineLatest] followed by [io.reactivex.Observable#flatMap]
 */
inline fun <T1 : Any, T2 : Any, T3 : Any, R : Any> flatMapLatest(
        observable1: Observable<T1>,
        observable2: Observable<T2>,
        observable3: Observable<T3>,
        crossinline function: (T1, T2, T3) -> Observable<out R>
): Observable<R> =
        Observables.combineLatest(observable1, observable2, observable3) { t1, t2, t3 -> Triple(t1, t2, t3) }
                .flatMap { (t1, t2, t3) -> function(t1, t2, t3) }


fun <T> Observable<T>.testWithLogging(): TestObserver<T> = this
        .doOnEach {
            println(it)
            (it.error as? CompositeException)?.let {
                println("Exceptions: ")
                it.exceptions.forEach(::println)
            }
        }
        .test()

fun <T> TestObserver<T>.assertOneOfValues(predicate: (T) -> Boolean) =
        values().find(predicate) != null