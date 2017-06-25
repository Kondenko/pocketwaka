package com.kondenko.pocketwaka.dagger.component

import com.kondenko.pocketwaka.BaseView
import com.kondenko.pocketwaka.dagger.module.BaseModule

interface BaseSubcomponent<T1 : BaseView, in T2 : BaseModule> {
    fun inject(view: T1)
}