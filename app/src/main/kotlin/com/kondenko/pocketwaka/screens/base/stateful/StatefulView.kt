package com.kondenko.pocketwaka.screens.base.stateful

import com.kondenko.pocketwaka.screens.base.BaseView

interface StatefulView<T> : BaseView {

    fun showModel(model: T)

    fun showLoading()

    fun showEmptyState()

}