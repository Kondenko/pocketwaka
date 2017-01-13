package com.kondenko.pocketwaka.ui

import android.content.Context
import android.support.v4.view.ViewCompat
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.github.mikephil.charting.charts.PieChart
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.api.model.stats.StatsItem

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
        val content = inflater.inflate(R.layout.layout_card_stats_content, card, true)

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
            TYPE_PROJECTS -> R.string.str_stats_projects
            TYPE_EDITORS -> R.string.str_stats_editors
            TYPE_LANGUAGES -> R.string.str_stats_languages
            TYPE_OPERATING_SYSTEMS -> R.string.str_stats_operating_systems
            else -> throw IllegalArgumentException("Cannot resolve card type. Make sure you are using ${javaClass.simpleName}.TYPE_ constants")
        })
    }

    fun setupList(content: View) {
        val list = content.findViewById(R.id.statsCardRecyclerView) as RecyclerView
        val adapter = CardStatsListAdapter(data)
        list.adapter = adapter
        list.layoutManager = NonScrollableLinearLayoutManager(context)
    }

    fun setupChart(content: View) {

    }

}