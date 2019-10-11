package com.kondenko.pocketwaka.utils.extensions

import com.kondenko.pocketwaka.utils.types.KOptional
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.Single
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

fun <T> Observable<T>.doOnComplete(onComplete: (List<T>) -> Unit): Observable<T> = compose {
    val buffer = mutableListOf<T>()
    it.doOnNext { buffer += it }.doOnComplete { onComplete(buffer) }
}

class AssertInOrder<T> {

    val predicates = mutableListOf<(T) -> Boolean>()

    fun assert(predicate: (T) -> Boolean) = predicates.add(predicate)

}

fun <T> TestObserver<T>.assertInOrder(assertions: AssertInOrder<T>.() -> Unit) {
    AssertInOrder<T>()
            .apply(assertions)
            .predicates
            .mapIndexed { index, predicate ->
                assertValueAt(index, predicate)
            }
}

operator fun <T> Observable<T>.plus(observable: Observable<T>) = this.concatWith(observable)

fun <T> Maybe<T>.toOptionalSingle(): Single<KOptional<T>> = this
        .map { KOptional.of(it) }
        .defaultIfEmpty(KOptional.empty())
        .toSingle()