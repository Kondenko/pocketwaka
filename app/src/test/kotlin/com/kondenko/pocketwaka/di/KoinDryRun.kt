package com.kondenko.pocketwaka.di

import org.junit.Test
import org.junit.runner.RunWith
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.test.KoinTest
import org.koin.test.dryRun
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class KoinDryRun : KoinTest {

    @Test
    fun testDi() {
        startKoin(modulesList(RuntimeEnvironment.application))
        dryRun()
    }

}