package com.kondenko.pocketwaka.utils.extensions

import android.view.View
import io.reactivex.Completable

fun post(vararg views: View) = Completable.merge(views.map(View::post))

fun View.post(): Completable {
    return Completable.create {
        post {
            it.onComplete()
        }
    }
}