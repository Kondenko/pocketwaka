package com.kondenko.pocketwaka.di

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.FirebaseApp
import com.kondenko.pocketwaka.domain.stats.model.StatsUiModel
import com.kondenko.pocketwaka.domain.summary.model.SummaryUiModel
import com.kondenko.pocketwaka.screens.main.MainViewModel
import com.kondenko.pocketwaka.screens.stats.StatsViewModel
import com.kondenko.pocketwaka.screens.stats.adapter.StatsAdapter
import com.kondenko.pocketwaka.screens.summary.SummaryAdapter
import com.kondenko.pocketwaka.screens.summary.SummaryViewModel
import com.kondenko.pocketwaka.ui.skeleton.RecyclerViewSkeleton
import com.kondenko.pocketwaka.utils.BrowserWindow
import com.kondenko.pocketwaka.utils.date.DateRange
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
        FirebaseApp.initializeApp(context)
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
                parametersOf(context, emptyList<StatsUiModel>())
            }
            create<RecyclerViewSkeleton<SummaryUiModel, SummaryAdapter>> {
                parametersOf(context, emptyList<SummaryUiModel>())
            }
            create<StatsViewModel> {
                parametersOf("7_days")
            }
            create<MainViewModel> {
                parametersOf(0)
            }
            create<SummaryViewModel> {
                parametersOf(mock<DateRange>())
            }
        }
    }

}