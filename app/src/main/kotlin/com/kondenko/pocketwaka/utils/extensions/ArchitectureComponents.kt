package com.kondenko.pocketwaka.utils.extensions

import androidx.lifecycle.*
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

fun <T> LiveData<T>.observe(lifecycleOwner: LifecycleOwner, observer: (T) -> Unit) =
        observe(lifecycleOwner, Observer(observer))

fun LifecycleOwner.toObservable(): Observable<Lifecycle.State> {
    val lifecycleEvents = PublishSubject.create<Lifecycle.State>()
    lifecycle.addObserver(object : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
        fun onAny() {
            lifecycleEvents.onNext(lifecycle.currentState)
        }
    })
    return lifecycleEvents
}