package com.kondenko.pocketwaka.screens.stats


import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kondenko.pocketwaka.Const
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.data.stats.model.StatsDataWrapper
import com.kondenko.pocketwaka.data.stats.model.StatsItem
import com.kondenko.pocketwaka.databinding.FragmentStatsDataBinding
import com.kondenko.pocketwaka.events.TabsAnimationEvent
import com.kondenko.pocketwaka.ui.CardStats
import com.kondenko.pocketwaka.ui.ObservableScrollView
import com.kondenko.pocketwaka.ui.OnScrollViewListener
import org.greenrobot.eventbus.EventBus
import java.util.*


class FragmentStatsData : Fragment() {

    private var shadowAnimationNeeded = true

    companion object {
        val ARG_STATS_DATA: String = "stats"

        fun newInstance(statsData: StatsDataWrapper): FragmentStatsData {
            val fragment = FragmentStatsData()
            val bundle = Bundle()
            bundle.putParcelable(ARG_STATS_DATA, statsData)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Setup data binding
        val statsDataObject: StatsDataWrapper? = arguments?.getParcelable(ARG_STATS_DATA)
        val binding = DataBindingUtil.inflate<FragmentStatsDataBinding>(inflater, R.layout.fragment_stats_data, container, false)
        statsDataObject?.let {
            binding.dataWrapper = statsDataObject
            addStatsCards(binding, statsDataObject)
        }
        // Make the tabs "float" over the other views
        binding.statsScrollView.setOnScrollListener(object : OnScrollViewListener {
            override fun onScrollChanged(scrollView: ObservableScrollView, x: Int, y: Int, oldX: Int, oldY: Int) {
                if (y >= 10) {
                    if (shadowAnimationNeeded) {
                        binding.shadowView.animate().alpha(Const.MAX_SHADOW_OPACITY)
                        EventBus.getDefault().post(TabsAnimationEvent(false))
                    }
                    shadowAnimationNeeded = false
                } else {
                    binding.shadowView.animate().alpha(0f)
                    EventBus.getDefault().post(TabsAnimationEvent(true))
                    shadowAnimationNeeded = true
                }
            }

        })
        return binding.root
    }

    private fun addStatsCards(binding: FragmentStatsDataBinding, statsDataWrapper: StatsDataWrapper) {
        if (binding.linearLayoutCards.childCount == 0) {
            val cards = getAvailableCards(statsDataWrapper)
            for (card in cards) {
                binding.linearLayoutCards.addView(card.getView())
            }
        }
    }

    fun getAvailableCards(statsDataWrapper: StatsDataWrapper): ArrayList<CardStats> {
        val cards = ArrayList<CardStats>()
        cards.addIfNotEmpty(statsDataWrapper.stats.projects, CardStats.TYPE_PROJECTS)
        cards.addIfNotEmpty(statsDataWrapper.stats.editors, CardStats.TYPE_EDITORS)
        cards.addIfNotEmpty(statsDataWrapper.stats.languages, CardStats.TYPE_LANGUAGES)
        cards.addIfNotEmpty(statsDataWrapper.stats.operatingSystems, CardStats.TYPE_OPERATING_SYSTEMS)
        return cards
    }

    private fun ArrayList<CardStats>.addIfNotEmpty(dataArray: List<StatsItem>?, type: Int) {
        if (dataArray != null && dataArray.isNotEmpty()) this.add(CardStats(context, type, dataArray))
    }

}
