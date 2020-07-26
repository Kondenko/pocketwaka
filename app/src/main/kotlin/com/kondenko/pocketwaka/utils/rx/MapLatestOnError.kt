package com.kondenko.pocketwaka.utils.rx

import io.reactivex.Observable
import io.reactivex.ObservableOperator
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

fun <T : Any> Observable<T>.mapLatestOnError(defaultItem: T? = null, scanMap: (T, Throwable?) -> T): Observable<T> =
      lift(MapLatestOnError(defaultItem, scanMap))


class MapLatestOnError<T>(private val defaultItem: T?, private val scanMap: (T, Throwable?) -> T) : ObservableOperator<T, T> {

    override fun apply(observer: Observer<in T>): Observer<in T> = ActualObserver(observer)

    private inner class ActualObserver(private val observer: Observer<in T>) : Observer<T> {

        private var previousValue: T? = null

        override fun onNext(next: T) {
            previousValue = next
            observer.onNext(next)
        }

        override fun onComplete() = observer.onComplete()

        override fun onSubscribe(d: Disposable) = observer.onSubscribe(d)

        override fun onError(e: Throwable) {
            try {
                previousValue?.let {
                    previousValue = scanMap(it, e).also(observer::onNext)
                } ?: defaultItem?.let {
                    onNext(defaultItem)
                    observer.onComplete()
                } ?: observer.onError(e)
            } catch (e: Throwable) {
                observer.onError(e)
            }
        }

    }

}