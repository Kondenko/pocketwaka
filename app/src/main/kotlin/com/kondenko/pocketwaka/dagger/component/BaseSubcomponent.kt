package com.kondenko.pocketwaka.dagger.component

import com.kondenko.pocketwaka.dagger.module.BaseModule
import com.kondenko.pocketwaka.screens.BaseView

interface BaseSubcomponent<T1 : BaseView, in T2 : BaseModule> {
    fun inject(view: T1)
}