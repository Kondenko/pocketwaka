package com.kondenko.pocketwaka.dagger.components

import com.kondenko.pocketwaka.dagger.modules.TestNetModule
import com.kondenko.pocketwaka.dagger.modules.TestServiceModule
import com.kondenko.pocketwaka.screens.main.MainActivityActivityPresenterTest
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(TestServiceModule::class, TestNetModule::class))
interface TestServiceComponent {
    fun inject(test: MainActivityActivityPresenterTest)
}