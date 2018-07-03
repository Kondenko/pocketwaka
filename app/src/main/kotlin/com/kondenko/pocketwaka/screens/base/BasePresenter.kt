package com.kondenko.pocketwaka.screens.base

import io.reactivex.disposables.Disposable

abstract class BasePresenter<V> {

    protected var view: V? = null

    open fun attach(view: V) {
        this.view = view
    }

    open fun detach() {
        view = null
    }

    protected fun dispose(vararg disposables: Disposable?) {
        disposables.forEach { it?.dispose() }
    }

}