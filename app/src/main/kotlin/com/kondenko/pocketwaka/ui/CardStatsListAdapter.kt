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
import com.kondenko.pocketwaka.domain.stats.model.StatsItem

class CardStatsListAdapter(val context: Context, val dataset: List<StatsItem>) : RecyclerView.Adapter<CardStatsListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_card_stats, parent, false)
        val viewHolder = ViewHolder(view)
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataset[position]
        val labelDrawable: Drawable? = context.resources?.getDrawable(R.drawable.stats_item_label)
        labelDrawable?.setColorFilter(item.color, PorterDuff.Mode.SRC_IN)
        holder.labelColor?.background = labelDrawable
        holder.header?.text = item.name
//        holder?.percentage?.text = getPercent(item.percent)
        setPercent(holder.percentage, item.percent!!)
    }

    /**
     * Set the percent value so that if it's less than 1% it looks like
     * <pale>less than</pale> <formatted percent value>
     *  E.g. "less than 0.1%@
     */
    private fun setPercent(textView: TextView?, percent: Double) {
        if (percent > 1) {
            textView?.text = String.format(context.getString(R.string.stats_card_percent_format_int) as String, percent)
        } else {
            textView?.text = context.getString(R.string.stats_card_less_than_1_percent)
        }
    }

    override fun getItemCount(): Int = dataset.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var labelColor = itemView.findViewById<View>(R.id.labelColor)
        var header = itemView.findViewById<TextView>(R.id.textItem)
        var percentage = itemView.findViewById<TextView>(R.id.textPercent)
    }

}