package com.kondenko.pocketwaka.screens.stats


import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.kondenko.pocketwaka.Const
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.utils.extensions.adjustForDensity
import com.kondenko.pocketwaka.utils.getStatusBarHeight
import com.ogaclejapan.smarttablayout.utils.v4.Bundler
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_stats_container.*
import timber.log.Timber
import kotlin.math.roundToInt


class FragmentStats : Fragment() {

    private val refreshEvents = PublishSubject.create<Any>()

    private var areTabsElevated = false

    private var scrollSubscription: Disposable? = null

    private var refreshSubscription: Disposable? = null

    private var elevatedSurface: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = inflater.inflate(R.layout.fragment_stats_container, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        elevatedSurface = activity?.findViewById(R.id.view_main_elevated_surface)
        val adapter = FragmentPagerItemAdapter(
                childFragmentManager,
                FragmentPagerItems.with(activity)
                        .addFragment(R.string.stats_tab_7_days, Const.STATS_RANGE_7_DAYS)
                        .addFragment(R.string.stats_tab_30_days, Const.STATS_RANGE_30_DAYS)
                        .addFragment(R.string.stats_tab_6_months, Const.STATS_RANGE_6_MONTHS)
                        .addFragment(R.string.stats_tab_1_year, Const.STATS_RANGE_1_YEAR)
                        .create()
        )
        stats_viewpager_content.adapter = adapter
        stats_viewpager_content.post {
            onFragmentSelected(adapter.getPage(stats_viewpager_content.currentItem) as FragmentStatsTab)
        }
        stats_smarttablayout_ranges.setViewPager(stats_viewpager_content)
        stats_smarttablayout_ranges.setOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                val selectedFragment = adapter.getPage(position) as FragmentStatsTab?
                if (selectedFragment == null) Timber.e("$selectedFragment at position $position is null")
                selectedFragment?.let { fragment -> onFragmentSelected(fragment) }
            }
        })
        stats_smarttablayout_ranges.post {
            elevatedSurface?.updateLayoutParams {
                val statusbarHeight: Float = activity?.run {
                    getStatusBarHeight()?.toFloat()
                            ?: resources.getDimension(R.dimen.height_all_statusbar_fallback)
                } ?: view.context.adjustForDensity(24)
                height = stats_smarttablayout_ranges
                        .run { bottom + height + statusbarHeight }
                        .roundToInt()
            }
        }
    }

    private fun onFragmentSelected(fragment: FragmentStatsTab) {
        if (fragment.isScrollviewOnTop() && areTabsElevated) animateTabs(elevated = false)
        else if (!fragment.isScrollviewOnTop() && !areTabsElevated) animateTabs(elevated = true)
        refreshSubscription?.dispose()
        scrollSubscription?.dispose()
        refreshSubscription = fragment.subscribeToRefreshEvents(refreshEvents)
        scrollSubscription = fragment.scrollDirection().subscribe { scrollDirection ->
            animateTabs(elevated = scrollDirection === ScrollDirection.Down)
        }
    }

    private fun animateTabs(elevated: Boolean) {
        areTabsElevated = elevated
        // Tabs background color
        @Suppress("UsePropertyAccessSyntax")
        elevatedSurface?.let {
            val colorResting = ContextCompat.getColor(context!!, R.color.color_app_bar_resting)
            val colorElevated = ContextCompat.getColor(context!!, R.color.color_app_bar_elevated)
            val colorAnim = ValueAnimator()
            with(colorAnim) {
                setDuration(Const.DEFAULT_ANIM_DURATION)
                setIntValues(if (elevated) colorResting else colorElevated, if (elevated) colorElevated else colorResting)
                setEvaluator(ArgbEvaluator())
                addUpdateListener { valueAnimator ->
                    it.setBackgroundColor(valueAnimator.animatedValue as Int)
                }
                start()
            }
            // Custom tabs elevation
            stats_view_shadow.animate().alpha(if (elevated) Const.MAX_SHADOW_OPACITY else 0f).start()
        }
    }

    fun subscribeToRefreshEvents(refreshEvents: Observable<Any>) {
        refreshEvents.subscribeWith(this.refreshEvents)
    }

    private fun FragmentPagerItems.Creator.addFragment(@StringRes title: Int, range: String): FragmentPagerItems.Creator {
        return this.add(title, FragmentStatsTab::class.java, Bundler().putString(FragmentStatsTab.ARG_RANGE, range).get())
    }

}
