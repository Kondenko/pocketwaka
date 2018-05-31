package com.kondenko.pocketwaka.screens.base.stateful

import com.kondenko.pocketwaka.screens.base.BaseView

interface StatefulView<T> : BaseView {

    fun onSuccess(result: T?)

    fun onRefresh()

    fun setLoading(isLoading: Boolean)

}