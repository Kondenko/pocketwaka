package com.kondenko.pocketwaka.screens.stats

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.domain.stats.model.StatsItem
import kotlinx.android.synthetic.main.item_stats_card.view.*

class CardStatsListAdapter(private val context: Context, private val items: List<StatsItem>) : RecyclerView.Adapter<CardStatsListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_stats_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: StatsItem) {
            with(itemView) {
                textview_stats_item_name.text = item.name
                setPercent(textview_stats_item_percent, item.percent!!)
                progressbar_stats_item.color = item.color
                progressbar_stats_item.progress = item.percent.toFloat() / 100
                // Add text label
                post {
                    val isTextWiderThanProgress = progressbar_stats_item.progressBarWidth <= textview_stats_item_name.measuredWidth
                    if (isTextWiderThanProgress) {
                        textview_stats_item_name.width = (progressbar_stats_item.measuredWidth - progressbar_stats_item.progressBarWidth).toInt()
                        textview_stats_item_name.setTextColor(ContextCompat.getColor(context, R.color.color_stats_item_dark))
                        textview_stats_item_name.x += progressbar_stats_item.progressBarWidth
                    } else {
                        textview_stats_item_name.width = progressbar_stats_item.progressBarWidth.toInt()
                    }
                }
            }
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

    }

}