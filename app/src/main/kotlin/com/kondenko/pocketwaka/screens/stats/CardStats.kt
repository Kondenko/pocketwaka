package com.kondenko.pocketwaka.screens.stats

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.domain.stats.model.StatsItem
import kotlinx.android.synthetic.main.item_card_stats.view.*

class CardStats(val context: Context, val title: String, val data: List<StatsItem>) {

    val view: View = LayoutInflater.from(context).inflate(R.layout.item_card_stats, null, true)

    init {
        view.statsCardHeader.text = title
        setList()
    }

    private fun setList() {
        view.statsCardRecyclerView.adapter = CardStatsListAdapter(context, data)
        view.statsCardRecyclerView.layoutManager = object : LinearLayoutManager(context) {
            override fun canScrollVertically() = false
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as CardStats
        if (title != other.title) return false
        if (data != other.data) return false
        return true
    }

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + data.hashCode()
        return result
    }

}