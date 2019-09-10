package com.kondenko.pocketwaka.screens.ranges.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.doOnPreDraw
import androidx.core.view.updateLayoutParams
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.domain.ranges.model.StatsItem
import com.kondenko.pocketwaka.screens.base.SkeletonAdapter
import com.kondenko.pocketwaka.ui.skeleton.Skeleton
import com.kondenko.pocketwaka.utils.extensions.getColorCompat
import com.kondenko.pocketwaka.utils.extensions.setInvisible
import kotlinx.android.synthetic.main.item_stats_item.view.*

class CardStatsListAdapter(context: Context, showSkeleton: Boolean) : SkeletonAdapter<StatsItem, CardStatsListAdapter.ViewHolder>(context, showSkeleton) {

    private data class TextParams(val width: Int, val x: Float, val useDarkColor: Boolean)

    private val textParamsCache = mutableMapOf<StatsItem, TextParams>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflate(R.layout.item_stats_item, parent)
        val skeleton = createSkeleton(view)
        return ViewHolder(view, skeleton)
    }

    override fun createSkeleton(view: View): Skeleton {
        val transformation = { v: View, isSkeleton: Boolean ->
            when (v.id) {
                R.id.textview_stats_item_name -> {
                    v.translationX += 8f.adjustValue(isSkeleton)
                }
            }
        }
        return Skeleton(context, view, transform = transformation)
    }

    inner class ViewHolder(itemView: View, skeleton: Skeleton) : SkeletonViewHolder<StatsItem>(itemView, skeleton) {

        override fun bind(item: StatsItem) {
            super.bind(item)
            with(itemView) {
                textview_stats_item_percent?.apply {
                    if (item.percent != null) setPercentValue(item.percent)
                    else if (!showSkeleton) setInvisible()
                }
                textview_stats_item_name.text = item.name
                progressbar_stats_item_percentage.color = item.color
                progressbar_stats_item_percentage.progress = (item.percent?.toFloat()
                        ?: 0f) / 100
                itemView.doOnPreDraw {
                    val params = getTextParams(
                            item,
                            progressbar_stats_item_percentage.width,
                            progressbar_stats_item_percentage.progressBarWidth,
                            textview_stats_item_name.width,
                            textview_stats_item_name.x
                    )
                    with(textview_stats_item_name) {
                        updateLayoutParams {
                            width = params.width
                            x = params.x
                        }
                        if (params.useDarkColor) {
                            setTextColor(context.getColorCompat(R.color.color_stats_item_dark))
                        }
                    }
                }
            }
        }

    }

    private fun getTextParams(item: StatsItem, progressBarWidth: Int, progressWidth: Float, textViewWidth: Int, textViewX: Float) =
            textParamsCache.getOrPut(item) { calculateNamePosition(item.percent, progressBarWidth, progressWidth, textViewWidth, textViewX) }

    /**
     * Puts stats item's name outside of the ProgressBar is the name is too long.
     */
    private fun calculateNamePosition(progress: Double?, progressBarWidth: Int, progressWidth: Float, textViewWidth: Int, textViewX: Float): TextParams {
        val isTextWiderThanProgress = progressWidth * 1.1 <= textViewWidth || progress ?: 0.0 <= 0.1
        return if (isTextWiderThanProgress && !showSkeleton) {
            val width = progressBarWidth - progressWidth.toInt()
            val x = textViewX + progressWidth
            TextParams(width, x, true)
        } else {
            val width = if (showSkeleton) progressBarWidth else progressWidth.toInt()
            TextParams(width, textViewX, false)
        }
    }

    /**
     * Set the percent value so that if it's less than 1% it looks like < 1%
     */
    private fun TextView.setPercentValue(percent: Double) {
        text = if (percent > 1) {
            String.format(context.getString(R.string.stats_card_percent_format_int) as String, percent)
        } else {
            context.getString(R.string.stats_card_less_than_1_percent)
        }
    }

}