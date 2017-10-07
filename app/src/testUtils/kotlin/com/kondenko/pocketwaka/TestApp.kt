package com.kondenko.pocketwaka

import android.app.Application
import com.kondenko.pocketwaka.dagger.components.DaggerTestServiceComponent
import com.kondenko.pocketwaka.dagger.components.TestServiceComponent
import com.kondenko.pocketwaka.dagger.modules.TestNetModule
import com.kondenko.pocketwaka.dagger.modules.TestServiceModule


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

