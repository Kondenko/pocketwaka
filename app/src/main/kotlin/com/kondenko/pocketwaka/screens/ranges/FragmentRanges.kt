package com.kondenko.pocketwaka.screens.ranges


import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.kondenko.pocketwaka.Const
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.screens.ranges.model.ScrollDirection
import com.kondenko.pocketwaka.screens.ranges.model.TabsElevationState
import com.kondenko.pocketwaka.utils.extensions.getColorCompat
import com.ogaclejapan.smarttablayout.utils.v4.Bundler
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_stats_container.*
import timber.log.Timber


class FragmentRanges : Fragment() {

    private val refreshEvents = PublishSubject.create<Any>()

    private var scrollSubscription: Disposable? = null

    private var refreshSubscription: Disposable? = null

    private val keyTabsElevation = "tabsElevationState"
    private lateinit var tabsElevation: TabsElevationState

    private lateinit var colorAnimator: ValueAnimator

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = inflater.inflate(R.layout.fragment_stats_container, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tabsElevation = savedInstanceState?.getParcelable(keyTabsElevation) ?: TabsElevationState()
        val toolbarColorResting = view.context.getColorCompat(R.color.color_app_bar_resting)
        val toolbarColorElevated = view.context.getColorCompat(R.color.color_app_bar_elevated)
        colorAnimator = createColorAnimator(activity as? AppCompatActivity, toolbarColorResting, toolbarColorElevated)
        setupViewPager()
        restoreTabsElevation(stats_viewpager_content.currentItem)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(keyTabsElevation, tabsElevation)
        super.onSaveInstanceState(outState)
    }

    private fun setupViewPager() {
        val pagerAdapter = FragmentPagerItemAdapter(
                childFragmentManager,
                FragmentPagerItems.with(activity)
                        .addFragment(R.string.stats_tab_7_days, Const.STATS_RANGE_7_DAYS)
                        .addFragment(R.string.stats_tab_30_days, Const.STATS_RANGE_30_DAYS)
                        .addFragment(R.string.stats_tab_6_months, Const.STATS_RANGE_6_MONTHS)
                        .addFragment(R.string.stats_tab_1_year, Const.STATS_RANGE_1_YEAR)
                        .create()
        )
        with(stats_viewpager_content) {
            offscreenPageLimit = 2
            this.adapter = pagerAdapter
            post {
                onFragmentSelected(currentItem, pagerAdapter.getPage(currentItem) as FragmentStatsTab)
            }
        }
        with(stats_smarttablayout_ranges) {
            setViewPager(stats_viewpager_content)
            setOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
                override fun onPageSelected(position: Int) {
                    val selectedFragment = pagerAdapter.getPage(position) as FragmentStatsTab?
                    if (selectedFragment != null) onFragmentSelected(position, selectedFragment)
                    else Timber.e("$selectedFragment at position $position is null")
                }
            })
        }
    }

    private fun createColorAnimator(activity: AppCompatActivity?, initialColor: Int, finalColor: Int) = ValueAnimator().apply {
        @Suppress("UsePropertyAccessSyntax")
        setDuration(Const.DEFAULT_ANIM_DURATION)
        setIntValues(initialColor, finalColor)
        setEvaluator(ArgbEvaluator())
        val toolbarBackgroundDrawable = ColorDrawable()
        activity?.supportActionBar?.setBackgroundDrawable(toolbarBackgroundDrawable)
        addUpdateListener { valueAnimator ->
            activity?.apply {
                val color = valueAnimator.animatedValue as Int
                window.statusBarColor = color
                toolbarBackgroundDrawable.color = color
                stats_smarttablayout_ranges.setBackgroundColor(color)
            }

        }
    }

    private fun onFragmentSelected(position: Int, fragment: FragmentStatsTab) {
        restoreTabsElevation(position)
        refreshSubscription?.dispose()
        scrollSubscription?.dispose()
        refreshSubscription = fragment.subscribeToRefreshEvents(refreshEvents)
        scrollSubscription = fragment.scrollDirection()
                .distinctUntilChanged()
                .skip(1) // Skip the initial ScrollDirection.Up causing an unwanted animation
                .subscribeBy(
                        onNext = { scrollDirection ->
                            animateTabs(scrollDirection == ScrollDirection.Down)
                        },
                        onError = Timber::e
                )
    }

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

    fun subscribeToRefreshEvents(refreshEvents: Observable<Any>): Observable<Any> = refreshEvents.subscribeWith(this.refreshEvents)

    private fun FragmentPagerItems.Creator.addFragment(@StringRes title: Int, range: String): FragmentPagerItems.Creator {
        return this.add(title, FragmentStatsTab::class.java, Bundler().putString(FragmentStatsTab.ARG_RANGE, range).get())
    }

}