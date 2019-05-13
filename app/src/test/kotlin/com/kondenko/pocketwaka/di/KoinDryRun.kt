package com.kondenko.pocketwaka.di

import androidx.test.core.app.ApplicationProvider
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.dsl.koinApplication
import org.koin.test.KoinTest
import org.koin.test.check.checkModules
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class KoinDryRun : KoinTest {

    @Test
    fun testDi() {
        koinApplication { modules(getModuleList(ApplicationProvider.getApplicationContext())) }
                .checkModules()
    }

}