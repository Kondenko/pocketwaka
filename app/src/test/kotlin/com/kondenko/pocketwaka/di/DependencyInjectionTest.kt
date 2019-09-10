package com.kondenko.pocketwaka.di

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kondenko.pocketwaka.di.qualifiers.Actual
import com.kondenko.pocketwaka.di.qualifiers.Skeleton
import com.kondenko.pocketwaka.domain.ranges.model.StatsUiModel
import com.kondenko.pocketwaka.screens.base.SkeletonAdapter
import com.kondenko.pocketwaka.screens.ranges.RangesViewModel
import com.kondenko.pocketwaka.screens.ranges.adapter.StatsAdapter
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.core.parameter.parametersOf
import org.koin.dsl.koinApplication
import org.koin.test.KoinTest
import org.koin.test.check.checkModules
import org.koin.test.mock.declareMock

@RunWith(AndroidJUnit4::class)
class DependencyInjectionTest : KoinTest {

    @Test
    fun `should build dependency graph`() {
        koinApplication {
            val context = ApplicationProvider.getApplicationContext<Context>()
            androidContext(context)
            modules(koinModules)
            declareMock<StatsAdapter>(Actual)
            declareMock<StatsAdapter>(Skeleton)
            declareMock<SkeletonAdapter<StatsUiModel, StatsAdapter.ViewHolder>>()
        }.checkModules {
            create<RangesViewModel> { parametersOf("7_days") }
        }
    }

}