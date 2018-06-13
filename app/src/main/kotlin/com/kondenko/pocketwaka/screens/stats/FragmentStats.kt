package com.kondenko.pocketwaka.screens.stats


import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kondenko.pocketwaka.Const
import com.kondenko.pocketwaka.R
import com.ogaclejapan.smarttablayout.utils.v4.Bundler
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_stats_container.*

class FragmentStats : Fragment() {

    private val refreshEvents = PublishSubject.create<Any>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
            = inflater.inflate(R.layout.fragment_stats_container, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
        stats_viewpager_content.offscreenPageLimit = 3
        stats_smarttablayout_ranges.setViewPager(stats_viewpager_content)
        for (position in 0 until adapter.count) {
            val fragment = adapter.getPage(position) as FragmentStatsTab
            fragment.subscribeToRefreshEvents(refreshEvents)
        }
        var currentSubscription: Disposable? = null
        stats_smarttablayout_ranges.setOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                val fragment = adapter.getPage(position) as FragmentStatsTab
                currentSubscription?.dispose()
                currentSubscription = fragment.scrollDirection().subscribe { scrollDirection ->
                    val colorGray = ContextCompat.getColor(context!!, R.color.color_background_gray)
                    val colorPrimaryLight = ContextCompat.getColor(context!!, android.R.color.white)
                    val colorAnim = ValueAnimator()
                    with(colorAnim) {
                        @Suppress("UsePropertyAccessSyntax")
                        setDuration(Const.DEFAULT_ANIM_DURATION)
                        setIntValues(if (scrollDirection.up) colorPrimaryLight else colorGray, if (scrollDirection.up) colorGray else colorPrimaryLight)
                        setEvaluator(ArgbEvaluator())
                        addUpdateListener { valueAnimator ->
                            stats_smarttablayout_ranges.setBackgroundColor(valueAnimator.animatedValue as Int)
                        }
                        start()
                    }
                }
            }
        })
    }

    fun subscribeToRefreshEvents(refreshEvents: Observable<Any>) {
        refreshEvents.subscribeWith(this.refreshEvents)
    }

    private fun FragmentPagerItems.Creator.addFragment(@StringRes title: Int, range: String)
       = this.add(title, FragmentStatsTab::class.java, Bundler().putString(FragmentStatsTab.ARG_RANGE, range).get())

}
