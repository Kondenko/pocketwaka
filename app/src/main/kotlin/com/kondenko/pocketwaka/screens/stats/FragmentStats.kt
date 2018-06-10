package com.kondenko.pocketwaka.screens.stats


import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kondenko.pocketwaka.Const
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.events.TabsAnimationEvent
import com.ogaclejapan.smarttablayout.utils.v4.Bundler
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems
import kotlinx.android.synthetic.main.fragment_stats_container.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class FragmentStats : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
            = inflater.inflate(R.layout.fragment_stats_container, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        stats_viewpager_content.adapter = FragmentPagerItemAdapter(
                childFragmentManager,
                FragmentPagerItems.with(activity)
                        .addFragment(R.string.stats_tab_7_days, Const.STATS_RANGE_7_DAYS)
                        .addFragment(R.string.stats_tab_30_days, Const.STATS_RANGE_30_DAYS)
                        .addFragment(R.string.stats_tab_6_months, Const.STATS_RANGE_6_MONTHS)
                        .addFragment(R.string.stats_tab_1_year, Const.STATS_RANGE_1_YEAR)
                        .create()
        )
        stats_viewpager_content.offscreenPageLimit = 3
        stats_smarttablayout_ranges.setViewPager(stats_viewpager_content)
    }

    private fun FragmentPagerItems.Creator.addFragment(@StringRes title: Int, range: String)
       = this.add(title, FragmentStatsTab::class.java, Bundler().putString(FragmentStatsTab.ARG_RANGE, range).get())

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onTabsAnimationEvent(event: TabsAnimationEvent) {
        val colorGray = ContextCompat.getColor(context!!, R.color.color_background_gray)
        val colorPrimaryLight = ContextCompat.getColor(context!!, android.R.color.white)
        val colorAnim = ValueAnimator()
        with(colorAnim) {
            setDuration(Const.DEFAULT_ANIM_DURATION)
            setIntValues(if (event.out) colorPrimaryLight else colorGray, if (event.out) colorGray else colorPrimaryLight)
            setEvaluator(ArgbEvaluator())
            addUpdateListener { valueAnimator ->
                stats_smarttablayout_ranges.setBackgroundColor(valueAnimator.animatedValue as Int)
            }
            start()
        }
    }

}
