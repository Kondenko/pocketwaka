package com.kondenko.pocketwaka.screens.stats


import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.kondenko.pocketwaka.Const
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.analytics.Event
import com.kondenko.pocketwaka.analytics.EventTracker
import com.kondenko.pocketwaka.analytics.Screen
import com.kondenko.pocketwaka.analytics.ScreenTracker
import com.kondenko.pocketwaka.screens.Refreshable
import com.kondenko.pocketwaka.screens.stats.model.ScrollDirection
import com.kondenko.pocketwaka.screens.stats.model.TabsElevationState
import com.kondenko.pocketwaka.utils.WakaLog
import com.kondenko.pocketwaka.utils.extensions.getColorCompat
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.fragment_stats_container.*
import org.koin.android.ext.android.inject


class FragmentStats : Fragment(), Refreshable {

    private val screenTracker: ScreenTracker by inject()

    private val eventTracker: EventTracker by inject()

    private lateinit var pagerAdapter: FragmentPagerItemAdapter

    private val refreshEvents = PublishSubject.create<Unit>()

    private var scrollSubscription: Disposable? = null

    private var refreshSubscription: Disposable? = null

    private lateinit var tabsElevation: TabsElevationState
    private val keyTabsElevation = "tabsElevationState"

    private lateinit var colorAnimator: ValueAnimator

    private var surfaceColorResting: Int? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = inflater.inflate(R.layout.fragment_stats_container, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tabsElevation = savedInstanceState?.getParcelable(keyTabsElevation) ?: TabsElevationState()
        val surfaceColorResting = view.context.getColorCompat(R.color.color_app_bar_resting)
        val surfaceColorElevated = view.context.getColorCompat(R.color.color_app_bar_elevated)
        this.surfaceColorResting = surfaceColorResting
        this.colorAnimator = createColorAnimator(activity as? AppCompatActivity, surfaceColorResting, surfaceColorElevated)
        setupViewPager()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden) {
            surfaceColorResting?.let {
                activity?.window?.statusBarColor = it
                (activity as? AppCompatActivity)?.appbar_main?.setBackgroundColor(it)
            }
        } else {
            restoreTabsElevation(stats_viewpager_content.currentItem)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(keyTabsElevation, tabsElevation)
        super.onSaveInstanceState(outState)
    }

    private fun setupViewPager() {
        pagerAdapter = FragmentPagerItemAdapter(
              childFragmentManager,
              FragmentPagerItems.with(activity)
                    .addFragment(R.string.stats_tab_7_days, Const.STATS_RANGE_7_DAYS)
                    .addFragment(R.string.stats_tab_30_days, Const.STATS_RANGE_30_DAYS)
                    .addFragment(R.string.stats_tab_6_months, Const.STATS_RANGE_6_MONTHS)
                    .addFragment(R.string.stats_tab_1_year, Const.STATS_RANGE_1_YEAR)
                    .create()
        )
        with(stats_viewpager_content) {
            offscreenPageLimit = 1
            adapter = pagerAdapter
        }
        with(stats_smarttablayout_ranges) {
            setViewPager(stats_viewpager_content)
            setOnTabClickListener {
                if (stats_viewpager_content.currentItem == it) {
                    (pagerAdapter.getPage(it) as? FragmentStatsTab)?.scrollToTop()
                }
            }
            setOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
                override fun onPageSelected(position: Int) {
                    onFragmentSelected(position)
                }
            })
        }
        stats_viewpager_content.doOnLayout {
            onFragmentSelected(0)
        }
    }

    @Suppress("UsePropertyAccessSyntax")
    private fun createColorAnimator(activity: AppCompatActivity?, initialColor: Int, finalColor: Int) =
          ValueAnimator.ofInt(initialColor, finalColor).apply {
              activity?.apply {
                  setDuration(Const.DEFAULT_ANIM_DURATION)
                  setEvaluator(ArgbEvaluator())
                  addUpdateListener {
                      val color = animatedValue as Int
                      window.statusBarColor = color
                      appbar_main?.setBackgroundColor(color)
                      framelayout_stats_tab_container?.setBackgroundColor(color)
                  }
              }
          }

    private fun onFragmentSelected(position: Int) =
          (pagerAdapter.getPage(position) as? FragmentStatsTab?)?.let { fragment ->
              screenTracker.log(activity, Screen.Stats(fragment.arguments?.getString(FragmentStatsTab.argRange)))
              restoreTabsElevation(position)
              refreshSubscription?.dispose()
              scrollSubscription?.dispose()
              refreshSubscription = fragment.subscribeToRefreshEvents(refreshEvents)
              scrollSubscription = fragment.scrollDirection()
                    .distinctUntilChanged()
                    .skip(1) // Skip first emission (ScrollDirection.Up) causing an unwanted animation
                    .subscribeBy(
                          onNext = { scrollDirection ->
                              animateTabs(scrollDirection == ScrollDirection.Down)
                          },
                          onError = {
                              WakaLog.e(it)
                          }
                    )
          } ?: WakaLog.w("Fragment at position $position is null")

    private fun restoreTabsElevation(currentTabIndex: Int) {
        val wasPreviousTabElevated = tabsElevation.run { isElevated && this.currentTabIndex != currentTabIndex }
        tabsElevation.currentTabIndex = currentTabIndex
        val isCurrentTabElevated = tabsElevation.isElevated
        if (!wasPreviousTabElevated && isCurrentTabElevated) animateTabs(true)
        else if (wasPreviousTabElevated && !isCurrentTabElevated) animateTabs(false)
    }

    private fun animateTabs(elevate: Boolean) {
        // Tabs background color
        tabsElevation.isElevated = elevate
        colorAnimator.cancel()
        with(colorAnimator) {
            if (elevate) start()
            else reverse()
        }
        // Custom tabs elevation
        stats_view_shadow.animate()
              .alpha(if (elevate) Const.MAX_SHADOW_OPACITY else 0f)
              .start()
    }

    fun getSelectedTab() =
          (pagerAdapter.getPage(stats_viewpager_content.currentItem) as? FragmentStatsTab?)?.range

    @SuppressLint("CheckResult")
    override fun subscribeToRefreshEvents(refreshEvents: Observable<Unit>): Disposable? {
        refreshEvents
              .doOnDispose { refreshSubscription?.dispose() }
              .doOnNext { eventTracker.log(Event.ManualUpdate) }
              .subscribeWith(this.refreshEvents)
        return refreshSubscription
    }

    private fun FragmentPagerItems.Creator.addFragment(@StringRes title: Int, range: String) =
          add(title, FragmentStatsTab::class.java, bundleOf(FragmentStatsTab.argRange to range))

}