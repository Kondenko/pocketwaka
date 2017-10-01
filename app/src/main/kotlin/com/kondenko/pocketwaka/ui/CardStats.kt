package com.kondenko.pocketwaka.ui

import android.content.Context
import android.support.v4.view.ViewCompat
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.data.stats.model.StatsItem
import com.kondenko.pocketwaka.ui.onelinesegmentedchart.OneLineSegmentedChart
import com.kondenko.pocketwaka.ui.onelinesegmentedchart.Segment
import java.util.*

class CardStats(val context: Context, val type: Int, val data: List<StatsItem>) {

    private var card: CardView = CardView(context)

    companion object {
        val TYPE_PROJECTS = 0
        val TYPE_EDITORS = 1
        val TYPE_LANGUAGES = 2
        val TYPE_OPERATING_SYSTEMS = 3
    }

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val params = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT)
        val content = inflater.inflate(R.layout.view_card_stats_content, card, true)

        params.gravity = Gravity.CENTER_VERTICAL
        card.useCompatPadding = true
        card.setPadding(8, 8, 8, 8)
        card.layoutParams = params
        ViewCompat.setElevation(card, 2f)

        setupHeader(content)
        setupList(content)
        setupChart(content)
    }

    fun getView() = card

    fun setupHeader(content: View) {
        val header = content.findViewById(R.id.statsCardHeader) as TextView
        header.text = context.getString(when (type) {
            TYPE_PROJECTS -> R.string.stats_card_header_projects
            TYPE_EDITORS -> R.string.stats_card_header_editors
            TYPE_LANGUAGES -> R.string.stats_card_header_languages
            TYPE_OPERATING_SYSTEMS -> R.string.stats_card_header_operating_systems
            else -> throw IllegalArgumentException("Cannot resolve card type. Make sure you are using ${javaClass.simpleName}.TYPE_ constants.")
        })
    }

    fun setupList(content: View) {
        val list = content.findViewById(R.id.statsCardRecyclerView) as RecyclerView
        val adapter = CardStatsListAdapter(context, data)
        list.adapter = adapter
        list.layoutManager = object: LinearLayoutManager(context) {
            override fun canScrollVertically() = false
        }
    }

    fun setupChart(content: View) {
        val chart = content.findViewById(R.id.chart) as OneLineSegmentedChart
        val segments = ArrayList<Segment>(data.size)
        data.mapTo(segments) { Segment(it.percent.toFloat(), it.color, it.name) }
        chart.setSegments(segments)
    }

}