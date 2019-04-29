package com.kondenko.pocketwaka.screens.stats


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.domain.stats.model.StatsItem
import com.kondenko.pocketwaka.domain.stats.model.StatsModel
import com.kondenko.pocketwaka.screens.base.stateful.ModelFragment
import com.kondenko.pocketwaka.ui.CardStats
import com.kondenko.pocketwaka.utils.elevation
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_stats_data.*
import java.util.*
import kotlin.math.roundToInt


class ModelFragmentStats : ModelFragment<StatsModel>() {

    private var shadowAnimationNeeded = true

    val scrollDirection: PublishSubject<ScrollingDirection> = PublishSubject.create<ScrollingDirection>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_stats_data, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        stats_group_bestday.elevation(resources.getDimension(R.dimen.elevation_stats_bestday))
        stats_observablescrollview.scrolls.subscribe {
            shadowAnimationNeeded = if (it.y >= 10) {
                if (shadowAnimationNeeded) {
                    scrollDirection.onNext(ScrollingDirection.Down)
                }
                false
            } else {
                scrollDirection.onNext(ScrollingDirection.Up)
                true
            }
        }
    }

    fun isScrollviewOnTop() = stats_observablescrollview?.scrollY?:0 == 0

    override fun onModelChanged(model: StatsModel) {
        stats_textview_time_total.text = model.humanReadableTotal
        stats_textview_daily_average.text = model.humanReadableDailyAverage
        if (model.bestDay != null) {
            bestday_textview_date.text = model.bestDay.date
            bestday_textview_time.text = model.bestDay.time
        } else {
            stats_group_bestday.visibility = View.GONE
        }
        addStatsCards(model)
    }

    private fun addStatsCards(stats: StatsModel) {
        val cards = getAvailableCards(stats)
        var prevViewId = R.id.stats_cardview_bestday
        cards.forEachIndexed { index, card ->
            if (index == 0) {
                val cs = ConstraintSet()
                with(cs) {
                    clone(stats_constraintlayout_content)
                    connect(R.id.stats_cardview_bestday, ConstraintSet.BOTTOM, card.view.id, ConstraintSet.TOP)
                    applyTo(stats_constraintlayout_content)
                }
            }
            val nextViewId = if (index < cards.size - 1) cards[index + 1].view.id else ConstraintSet.PARENT_ID
            val nextViewSide = if (nextViewId == ConstraintSet.PARENT_ID) ConstraintSet.BOTTOM else ConstraintSet.TOP
            addCardView(prevViewId, nextViewId, card.view, nextViewSide)
            prevViewId = card.view.id
        }
    }

    private fun addCardView(prevViewId: Int, nextViewId: Int, view: View, nextViewSide: Int) {
        if (stats_constraintlayout_content.findViewById<View>(view.id) == null) {
            view.layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_CONSTRAINT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
            stats_constraintlayout_content.addView(view)
            val constraintSet = ConstraintSet()
            with(constraintSet) {
                val marginVertical = resources.getDimension(R.dimen.margin_all_card_outer_vertical).roundToInt()
                val marginHorizontal = resources.getDimension(R.dimen.margin_all_card_outer_horizontal).roundToInt()
                clone(stats_constraintlayout_content)
                connect(view.id, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, marginHorizontal)
                connect(view.id, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, marginHorizontal)
                connect(view.id, ConstraintSet.TOP, prevViewId, ConstraintSet.BOTTOM, marginVertical)
                connect(view.id, ConstraintSet.BOTTOM, nextViewId, nextViewSide, marginVertical)
                applyTo(stats_constraintlayout_content)
            }
        }
    }

    private fun getAvailableCards(stats: StatsModel): ArrayList<CardStats> {
        val cards = ArrayList<CardStats>()
        cards.addIfNotEmpty(stats.projects, getString(R.string.stats_card_header_projects))
        cards.addIfNotEmpty(stats.editors, getString(R.string.stats_card_header_editors))
        cards.addIfNotEmpty(stats.languages, getString(R.string.stats_card_header_languages))
        cards.addIfNotEmpty(stats.operatingSystems, getString(R.string.stats_card_header_operating_systems))
        return cards
    }

    private fun ArrayList<CardStats>.addIfNotEmpty(dataArray: List<StatsItem>?, title: String) {
        if (dataArray != null && dataArray.isNotEmpty()) {
            val card = CardStats(context!!, title, dataArray)
            card.view.id = card.hashCode()
            this.add(card)
        }
    }


}
