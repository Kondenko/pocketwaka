package com.kondenko.pocketwaka.di

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kondenko.pocketwaka.screens.ranges.RangesViewModel
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.parameter.parametersOf
import org.koin.dsl.koinApplication
import org.koin.test.KoinTest
import org.koin.test.check.checkModules

@RunWith(AndroidJUnit4::class)
class DependencyInjectionTest : KoinTest {

    @Test
    fun `should build dependency graph`() {
        koinApplication { modules(getModuleList(ApplicationProvider.getApplicationContext())) }
                .checkModules {
                    create<RangesViewModel> { parametersOf("7_days") }
                }
    }

}