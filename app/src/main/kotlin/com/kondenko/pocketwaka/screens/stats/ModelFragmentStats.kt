package com.kondenko.pocketwaka.screens.stats


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kondenko.pocketwaka.Const
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.domain.stats.model.BestDay
import com.kondenko.pocketwaka.domain.stats.model.StatsItem
import com.kondenko.pocketwaka.domain.stats.model.StatsModel
import com.kondenko.pocketwaka.events.TabsAnimationEvent
import com.kondenko.pocketwaka.screens.base.stateful.ARG_MODEL
import com.kondenko.pocketwaka.screens.base.stateful.ModelFragment
import com.kondenko.pocketwaka.ui.CardStats
import com.kondenko.pocketwaka.ui.ObservableScrollView
import com.kondenko.pocketwaka.ui.OnScrollViewListener
import kotlinx.android.synthetic.main.fragment_stats_data.*
import kotlinx.android.synthetic.main.layout_stats_best_day.*
import org.greenrobot.eventbus.EventBus
import java.util.*
import java.util.concurrent.TimeUnit


class ModelFragmentStats : ModelFragment<StatsModel>() {

    companion object {

        fun create(model: StatsModel): ModelFragment<StatsModel> {
            val fragment = ModelFragmentStats()
            val bundle = Bundle()
            bundle.putParcelable(ARG_MODEL, model)
            fragment.arguments = bundle
            return fragment
        }

    }


    private var shadowAnimationNeeded = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_stats_data, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        model = arguments!!.getParcelable(ARG_MODEL)
        displayModel(model)
        // Make the tabs "float" over the other views
        stats_observablescrollview.setOnScrollListener(object : OnScrollViewListener {
            override fun onScrollChanged(scrollView: ObservableScrollView, x: Int, y: Int, oldX: Int, oldY: Int) {
                if (y >= 10) {
                    if (shadowAnimationNeeded) {
                        stats_view_shadow?.animate()?.alpha(Const.MAX_SHADOW_OPACITY)
                        EventBus.getDefault().post(TabsAnimationEvent(false))
                    }
                    shadowAnimationNeeded = false
                } else {
                    stats_view_shadow?.animate()?.alpha(0f)
                    EventBus.getDefault().post(TabsAnimationEvent(true))
                    shadowAnimationNeeded = true
                }
            }
        })
    }

    override fun displayModel(model: StatsModel) {
        stats_textview_time_total.text = model.humanReadableTotal
        stats_textview_daily_average.text = model.humanReadableDailyAverage
        if (model.bestDay != null && model.bestDay.totalSeconds ?: 0 > 0) {
            bestday_textview_time.text = model.bestDay.getHumanReadableTime()
            bestday_textview_date.text = model.bestDay.date
        } else {
            bestday_constraintlayout_container?.visibility = View.GONE
        }
        addStatsCards(model)
    }

    private fun addStatsCards(stats: StatsModel) {
        if (stats_linearlayout_cards.childCount == 0) {
            val cards = getAvailableCards(stats)
            for (card in cards) {
                stats_linearlayout_cards.addView(card.getView())
            }
        }
    }

    private fun getAvailableCards(stats: StatsModel): ArrayList<CardStats> {
        val cards = ArrayList<CardStats>()
        cards.addIfNotEmpty(stats.projects, CardStats.TYPE_PROJECTS)
        cards.addIfNotEmpty(stats.editors, CardStats.TYPE_EDITORS)
        cards.addIfNotEmpty(stats.languages, CardStats.TYPE_LANGUAGES)
        cards.addIfNotEmpty(stats.operatingSystems, CardStats.TYPE_OPERATING_SYSTEMS)
        return cards
    }

    private fun ArrayList<CardStats>.addIfNotEmpty(dataArray: List<StatsItem>?, type: Int) {
        if (dataArray != null && dataArray.isNotEmpty()) this.add(CardStats(context!!, type, dataArray))
    }

    fun BestDay.getHumanReadableTime(): String {
        val pattern = context!!.getString(R.string.stats_time_format)
        val hours = TimeUnit.SECONDS.toHours(totalSeconds?.toLong() ?: 0)
        val minutes = TimeUnit.SECONDS.toMinutes(totalSeconds?.toLong() ?: 0) - TimeUnit.HOURS.toMinutes(hours)
        return String.format(pattern, hours, minutes)
    }


}
