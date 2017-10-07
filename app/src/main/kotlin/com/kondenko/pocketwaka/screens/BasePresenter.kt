package com.kondenko.pocketwaka.screens

open class BasePresenter<V> {

    protected var view: V? = null

    fun attach(view: V) {
        this.view = view
    }

    fun detach() {
        view = null
    }

}