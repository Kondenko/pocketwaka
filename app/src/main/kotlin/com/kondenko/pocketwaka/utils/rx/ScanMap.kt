package com.kondenko.pocketwaka.utils.rx

import io.reactivex.Observable
import io.reactivex.ObservableOperator
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

fun <T, R> Observable<T>.scanMap(initialValue: T? = null, scanMap: (T, T) -> R): Observable<R> =
      lift(ScanMap(initialValue, scanMap))

class ScanMap<T, R>(private val initialValue: T? = null, private val scanMap: (T, T) -> R) : ObservableOperator<R, T> {

    override fun apply(observer: Observer<in R>): Observer<in T> = ActualObserver(observer)

    private inner class ActualObserver(private val observer: Observer<in R>) : Observer<T> {

        private var previousValue: T? = initialValue

        override fun onNext(next: T) {
            previousValue?.let { prev -> observer.onNext(scanMap(prev, next)) }
            previousValue = next
        }

        override fun onComplete() = observer.onComplete()

        override fun onSubscribe(d: Disposable) = observer.onSubscribe(d)

        override fun onError(e: Throwable) = observer.onError(e)

    }

}