package com.kondenko.pocketwaka.ui

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.api.model.stats.StatsItem
import com.kondenko.pocketwaka.utils.SpanFormatter
import com.kondenko.pocketwaka.utils.Utils

class CardStatsListAdapter(val context: Context, val dataset: List<StatsItem>) : RecyclerView.Adapter<CardStatsListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_card_stats, parent, false)
        val viewHolder = ViewHolder(view)
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val item = dataset[position]
        val labelDrawable: Drawable? = context.resources?.getDrawable(R.drawable.stats_item_label)
        labelDrawable?.setColorFilter(item.color, PorterDuff.Mode.SRC_IN)
        holder?.labelColor?.background = labelDrawable
        holder?.header?.text = item.name
//        holder?.percentage?.text = getPercent(item.percent)
        setPercent(holder?.percentage, item.percent)
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
            val percentValue: Float = 1f
            // Colors
            val colorPrimary = Utils.getColor(context, R.color.color_text_black_secondary)
            val colorSecondary = Utils.getColor(context, R.color.color_text_black_pale)
            // Strings
            val stringLessThan = context.getString(R.string.stats_card_less_than)
            val stringPercent = String.format(context.getString(R.string.stats_card_percent_format_int) as String, percentValue)
            // Spannable strings
            val spannableLessThan = SpannableString(stringLessThan)
            val spannablePercent = SpannableString(stringPercent)
            spannableLessThan.setSpan(ForegroundColorSpan(colorSecondary), 0, stringLessThan.length, 0)
            spannablePercent.setSpan(ForegroundColorSpan(colorPrimary), 0, stringPercent.length, 0)
            // Resulting text
            val formatLessThan: String = context.getString(R.string.stats_card_less_than_format)
            textView?.text = SpanFormatter.format(formatLessThan, spannableLessThan, spannablePercent)
        }
    }

    private fun getPercent(percent: Double): String {
        val value: String
        if (percent < 1) value = String.format(context.getString(R.string.stats_card_percent_format_two_digit) as String, percent)
        else value = String.format(context.getString(R.string.stats_card_percent_format_int) as String, percent)
        return value
    }

    override fun getItemCount(): Int = dataset.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var labelColor = itemView.findViewById(R.id.labelColor)
        var header = itemView.findViewById(R.id.textItem) as TextView
        var percentage = itemView.findViewById(R.id.textPercent) as TextView
    }

}