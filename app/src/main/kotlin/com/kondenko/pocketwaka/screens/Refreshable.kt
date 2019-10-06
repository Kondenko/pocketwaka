package com.kondenko.pocketwaka.screens

import io.reactivex.Observable
import io.reactivex.disposables.Disposable

interface Refreshable {
    fun subscribeToRefreshEvents(refreshEvents: Observable<Any>): Disposable?
}