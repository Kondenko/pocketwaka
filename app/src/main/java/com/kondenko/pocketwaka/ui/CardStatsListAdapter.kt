package com.kondenko.pocketwaka.ui

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
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
        val view = LayoutInflater.from(context).inflate(R.layout.item_card_stats, parent, false)
        val viewHolder = ViewHolder(view)
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val item = dataset[position]
        val labelDrawable: Drawable? = context?.resources?.getDrawable(R.drawable.stats_item_label)
        labelDrawable?.setColorFilter(item.color, PorterDuff.Mode.SRC_IN)
        holder?.labelColor?.background = labelDrawable
        holder?.header?.text = item.name
        holder?.percentage?.text = String.format(context?.getString(R.string.stats_card_percent_format) as String, item.percent)
    }

    override fun getItemCount(): Int = dataset.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var labelColor = itemView.findViewById(R.id.labelColor)
        var header = itemView.findViewById(R.id.textItem) as TextView
        var percentage = itemView.findViewById(R.id.textPercent) as TextView
    }

}