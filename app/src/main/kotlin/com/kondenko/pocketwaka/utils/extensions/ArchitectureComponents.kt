package com.kondenko.pocketwaka.utils.extensions

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.toPublisher
import io.reactivex.Observable

fun <T> LiveData<T>.observe(lifecycleOwner: LifecycleOwner, observer: (T) -> Unit) =
        observe(lifecycleOwner, Observer(observer))

fun <T> LiveData<T>.toObservable(lifecycleOwner: LifecycleOwner) =
      Observable.fromPublisher(this.toPublisher(lifecycleOwner))