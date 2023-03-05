package com.kondenko.pocketwaka.di

import android.content.Context
import android.os.Looper.getMainLooper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.FirebaseApp
import com.kondenko.pocketwaka.domain.stats.model.StatsUiModel
import com.kondenko.pocketwaka.domain.summary.model.SummaryUiModel
import com.kondenko.pocketwaka.screens.main.MainViewModel
import com.kondenko.pocketwaka.screens.stats.FragmentStats
import com.kondenko.pocketwaka.screens.stats.StatsViewModel
import com.kondenko.pocketwaka.screens.stats.adapter.StatsAdapter
import com.kondenko.pocketwaka.screens.summary.FragmentSummary
import com.kondenko.pocketwaka.screens.summary.SummaryAdapter
import com.kondenko.pocketwaka.screens.summary.SummaryViewModel
import com.kondenko.pocketwaka.ui.skeleton.RecyclerViewSkeleton
import com.kondenko.pocketwaka.utils.BrowserWindow
import com.kondenko.pocketwaka.utils.date.DateRange
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import org.koin.dsl.koinApplication
import org.koin.test.ClosingKoinTest
import org.koin.test.check.checkModules
import org.koin.test.get
import org.koin.test.mock.MockProviderRule
import org.koin.test.mock.declareMock
import org.mockito.Mockito
import org.robolectric.Shadows.shadowOf

@RunWith(AndroidJUnit4::class)
class DependencyInjectionTest : ClosingKoinTest {

    @get:Rule
    val mockProviderRule = MockProviderRule.create { clazz ->
        Mockito.mock(clazz.java)
    }

    @Test
    fun `should build dependency graph`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        FirebaseApp.initializeApp(context)
        koinApplication {
            androidContext(context)
            shadowOf(getMainLooper()).idle()
            declareMock<LifecycleOwner> {
                whenever(lifecycle) doReturn declareMock<Lifecycle>()
            }
            modules(koinModules)
            checkModules {
                withScopeLink(named<FragmentStats>(), named<FragmentSummary>())
                withParameters<BrowserWindow> {
                    parametersOf(context, get<LifecycleOwner>())
                }
                withParameters<StatsAdapter> {
                    parametersOf(context, false)
                }
                withParameters<SummaryAdapter> {
                    parametersOf(context, false)
                }
                withParameters<RecyclerViewSkeleton<StatsUiModel, StatsAdapter>> {
                    parametersOf(context, emptyList<StatsUiModel>())
                }
                withParameters<RecyclerViewSkeleton<SummaryUiModel, SummaryAdapter>> {
                    parametersOf(context, emptyList<SummaryUiModel>())
                }
                withParameters<StatsViewModel> {
                    parametersOf("7_days")
                }
                withParameters<MainViewModel> {
                    parametersOf(0)
                }
                withParameters<SummaryViewModel> {
                    parametersOf(DateRange.PredefinedRange.Today.range)
                }
            }
        }
    }
}