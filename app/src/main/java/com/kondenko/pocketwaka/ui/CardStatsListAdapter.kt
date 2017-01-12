package com.kondenko.pocketwaka.ui

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.api.model.stats.StatsItem

class CardStatsListAdapter(val dataset: List<StatsItem>) : RecyclerView.Adapter<CardStatsListAdapter.ViewHolder>() {

    var context: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        context = parent?.context
        val view = LayoutInflater.from(context).inflate(R.layout.list_item_card_stats, parent, false)
        val viewHolder = ViewHolder(view)
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val item = dataset[position]
        holder?.labelColor?.background = context?.resources?.getDrawable(R.drawable.stats_list_item_label) // TODO Use different colors for different items
        holder?.header?.text = item.name
        holder?.percentage?.text = String.format(context?.getString(R.string.str_stats_percent) as String, item.percent)
    }

    override fun getItemCount(): Int = dataset.size

    private fun correctPercent(percent: Double): Double = if (percent < 1) percent else Math.round(percent).toDouble()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var labelColor = itemView.findViewById(R.id.labelColor)
        var header = itemView.findViewById(R.id.textItem) as TextView
        var percentage = itemView.findViewById(R.id.textPercent) as TextView
    }

}