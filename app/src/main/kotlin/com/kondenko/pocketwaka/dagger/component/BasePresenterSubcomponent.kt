package com.kondenko.pocketwaka.dagger.component

import com.kondenko.pocketwaka.BaseView
import com.kondenko.pocketwaka.dagger.module.BasePresenterModule

interface BasePresenterSubcomponent<T1 : BaseView, in T2 : BasePresenterModule> {
    fun inject(view: T1)
}