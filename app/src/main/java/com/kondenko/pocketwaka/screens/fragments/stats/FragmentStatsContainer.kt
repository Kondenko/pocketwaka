package com.kondenko.pocketwaka.screens.fragments.stats


import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kondenko.pocketwaka.Const
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.events.ErrorEvent
import com.kondenko.pocketwaka.events.SuccessEvent
import com.kondenko.pocketwaka.events.TabsAnimationEvent
import com.ogaclejapan.smarttablayout.SmartTabLayout
import com.ogaclejapan.smarttablayout.utils.v4.Bundler
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class FragmentStatsContainer : Fragment() {

    private val TAG = "FragmentStatsContainer"

    private lateinit var smartTabLayout: SmartTabLayout
    private lateinit var viewPager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_stats_container, container, false)
        viewPager = view?.findViewById(R.id.viewPager) as ViewPager
        smartTabLayout = view?.findViewById(R.id.tabLayout) as SmartTabLayout
        return view
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
        viewPager.adapter = FragmentPagerItemAdapter(
                childFragmentManager, FragmentPagerItems.with(activity)
                .add(R.string.str_stats_7_days, FragmentStats::class.java, Bundler().putString(Const.STATS_RANGE_KEY, Const.STATS_RANGE_7_DAYS).get())
                .add(R.string.str_stats_30_days, FragmentStats::class.java, Bundler().putString(Const.STATS_RANGE_KEY, Const.STATS_RANGE_30_DAYS).get())
                .add(R.string.str_stats_6_months, FragmentStats::class.java, Bundler().putString(Const.STATS_RANGE_KEY, Const.STATS_RANGE_6_MONTHS).get())
                .add(R.string.str_stats_1_year, FragmentStats::class.java, Bundler().putString(Const.STATS_RANGE_KEY, Const.STATS_RANGE_1_YEAR).get())
                .create()
        )
        smartTabLayout.setViewPager(viewPager)
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onTabsAnimationEvent(event: TabsAnimationEvent) {
        val colorGray = resources.getColor(R.color.colorBackgroundGray)
        val colorPrimaryLight = resources.getColor(android.R.color.white)
        val colorAnim = ValueAnimator()
        with(colorAnim) {
            setDuration(Const.DEFAULT_ANIM_DURATION)
            setIntValues(if (event.out) colorPrimaryLight else colorGray, if (event.out) colorGray else colorPrimaryLight)
            setEvaluator(ArgbEvaluator())
            addUpdateListener { valueAnimator ->
                smartTabLayout.setBackgroundColor(valueAnimator.animatedValue as Int)
            }
            start()
        }
    }


}
