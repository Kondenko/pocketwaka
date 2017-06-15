package com.kondenko.pocketwaka.dagger.component

import com.kondenko.pocketwaka.dagger.module.TestNetModule
import com.kondenko.pocketwaka.dagger.module.TestServiceModule
import com.kondenko.pocketwaka.screens.activities.main.MainActivityPresenterTest
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(TestServiceModule::class, TestNetModule::class))
interface TestServiceComponent {
    fun inject(test: MainActivityPresenterTest)
}