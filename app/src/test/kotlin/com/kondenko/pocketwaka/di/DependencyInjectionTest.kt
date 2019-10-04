package com.kondenko.pocketwaka.di

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kondenko.pocketwaka.domain.daily.model.SummaryUiModel
import com.kondenko.pocketwaka.domain.ranges.model.StatsUiModel
import com.kondenko.pocketwaka.screens.daily.SummaryAdapter
import com.kondenko.pocketwaka.screens.ranges.RangesViewModel
import com.kondenko.pocketwaka.screens.ranges.adapter.StatsAdapter
import com.kondenko.pocketwaka.ui.skeleton.RecyclerViewSkeleton
import com.kondenko.pocketwaka.utils.BrowserWindow
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.core.parameter.parametersOf
import org.koin.dsl.koinApplication
import org.koin.test.KoinTest
import org.koin.test.check.checkModules

@RunWith(AndroidJUnit4::class)
class DependencyInjectionTest : KoinTest {

    @Test
    fun `should build dependency graph`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val lifecycleOwner: LifecycleOwner = mock()
        val lifecycle: Lifecycle = mock()
        whenever(lifecycleOwner.lifecycle) doReturn lifecycle
        koinApplication {
            androidContext(context)
            modules(koinModules)
        }.checkModules {
            create<BrowserWindow> {
                parametersOf(context, lifecycleOwner)
            }
            create<StatsAdapter> {
                parametersOf(context, false)
            }
            create<SummaryAdapter> {
                parametersOf(context, false)
            }
            create<RecyclerViewSkeleton<StatsUiModel, StatsAdapter>> {
                parametersOf(mock<RecyclerView>(), context, emptyList<StatsUiModel>())
            }
            create<RecyclerViewSkeleton<SummaryUiModel, SummaryAdapter>> {
                parametersOf(mock<RecyclerView>(), context, emptyList<SummaryUiModel>())
            }
            create<RangesViewModel> {
                parametersOf("7_days")
            }
        }
    }

}