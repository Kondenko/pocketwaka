package com.kondenko.pocketwaka.screens

interface StatefulView<T> : BaseView {

    fun onSuccess(result: T?)

    fun onRefresh()

    fun setLoading(isLoading: Boolean)

}