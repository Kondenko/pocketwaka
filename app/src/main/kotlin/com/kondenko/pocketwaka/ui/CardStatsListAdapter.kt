package com.kondenko.pocketwaka.ui

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.domain.stats.model.StatsItem

class CardStatsListAdapter(private val context: Context, private val items: List<StatsItem>) : androidx.recyclerview.widget.RecyclerView.Adapter<CardStatsListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_stats_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val labelDrawable: Drawable? = context.resources?.getDrawable(R.drawable.stats_item_label)
        labelDrawable?.setColorFilter(item.color, PorterDuff.Mode.SRC_IN)
        holder.labelColor.background = labelDrawable
        holder.header.text = item.name
        setPercent(holder.percentage, item.percent!!)
    }

    /**
     * Set the percent value so that if it's less than 1% it looks like < 1%
     */
    private fun setPercent(textView: TextView?, percent: Double) {
        if (percent > 1) {
            textView?.text = String.format(context.getString(R.string.stats_card_percent_format_int) as String, percent)
        } else {
            textView?.text = context.getString(R.string.stats_card_less_than_1_percent)
        }
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        var labelColor: View = itemView.findViewById(R.id.labelColor)
        var header: TextView = itemView.findViewById(R.id.textItem)
        var percentage: TextView = itemView.findViewById(R.id.textPercent)
    }

}