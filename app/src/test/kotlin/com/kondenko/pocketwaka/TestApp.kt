package com.kondenko.pocketwaka

import android.app.Application
import com.kondenko.pocketwaka.dagger.component.DaggerTestServiceComponent
import com.kondenko.pocketwaka.dagger.component.TestServiceComponent
import com.kondenko.pocketwaka.dagger.module.TestNetModule
import com.kondenko.pocketwaka.dagger.module.TestServiceModule


class TestApp : Application() {

    companion object {
        @JvmStatic
        lateinit var testServiceComponent: TestServiceComponent
    }

    init {
        testServiceComponent = DaggerTestServiceComponent.builder()
                .testNetModule(TestNetModule())
                .testServiceModule(TestServiceModule())
                .build()
    }
}

