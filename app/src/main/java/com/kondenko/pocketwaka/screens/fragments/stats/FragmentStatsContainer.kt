package com.kondenko.pocketwaka.screens.fragments.stats


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kondenko.pocketwaka.Const
import com.kondenko.pocketwaka.R
import com.ogaclejapan.smarttablayout.SmartTabLayout
import com.ogaclejapan.smarttablayout.utils.v4.Bundler
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems


class FragmentStatsContainer : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_stats_container, container, false)
        val adapter = FragmentPagerItemAdapter(
                activity.supportFragmentManager, FragmentPagerItems.with(activity)
                .add(R.string.str_stats_7_days, FragmentStats::class.java, Bundler().putString(Const.STATS_RANGE_KEY, Const.STATS_RANGE_7_DAYS).get())
                .add(R.string.str_stats_14_days, FragmentStats::class.java, Bundler().putString(Const.STATS_RANGE_KEY, Const.STATS_RANGE_30_DAYS).get())
                .add(R.string.str_stats_6_months, FragmentStats::class.java, Bundler().putString(Const.STATS_RANGE_KEY, Const.STATS_RANGE_6_MONTHS).get())
                .add(R.string.str_stats_1_year, FragmentStats::class.java, Bundler().putString(Const.STATS_RANGE_KEY, Const.STATS_RANGE_1_YEAR).get())
                .add(R.string.str_stats_today, FragmentStatsToday::class.java) // TODO Move to the first place and use different data and layout
                .create()
        )
        val smartTabLayout = view?.findViewById(R.id.tabLayout) as SmartTabLayout
        val viewPager = view?.findViewById(R.id.viewPager) as ViewPager
        viewPager.adapter = adapter
        smartTabLayout.setViewPager(viewPager)
        return view
    }

}
