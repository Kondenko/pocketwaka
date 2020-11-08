package com.kondenko.pocketwaka.utils.extensions

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.kondenko.pocketwaka.screens.State
import io.reactivex.*
import io.reactivex.disposables.Disposable
import io.reactivex.exceptions.CompositeException
import io.reactivex.observers.TestObserver
import java.util.concurrent.TimeUnit

fun <T> T?.toMaybe(): Maybe<T> = if (this != null) Maybe.just(this) else Maybe.empty()

fun <T> T.toSingle(): Single<T> = Single.just(this)

fun <T> Observable<T>.testWithLogging(): TestObserver<T> = this
      .doOnEach {
          println(it)
          (it.error as? CompositeException)?.let {
              println("Exceptions: ")
              it.exceptions.forEach(::println)
          }
      }
      .test()

fun <T, R> Observable<T>.concatMapEagerDelayError(mapper: (T) -> ObservableSource<out R>): Observable<R> =
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

fun <T> Observable<T>.returnAfterCompletion(onComplete: (T?) -> Observable<T>): Observable<T> = compose { observable ->
    var lastItem: T? = null
    observable
          .doOnNext { lastItem = it }
          .materialize()
          .flatMap {
              if (it.isOnComplete) {
                  Observable.concatArray(
                        onComplete(lastItem).materialize(),
                        Observable.just(Notification.createOnComplete())
                  )
              } else {
                  Observable.just(it)
              }
          }
          .dematerialize()
}

operator fun <T> Observable<T>.plus(observable: Observable<T>) = this.concatWith(observable)

fun Disposable?.attachToLifecycle(lifecycle: LifecycleOwner) {
    lifecycle.lifecycle.addObserver(object : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun onDestroy() {
            this@attachToLifecycle?.dispose()
        }
    })
}

fun <T> Observable<State<T>>.debounceStateUpdates(timeout: Long = 50, scheduler: Scheduler): Observable<State<T>> =
      compose { it.debounce(timeout, TimeUnit.MILLISECONDS, scheduler) }

/* Assert in order */

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
