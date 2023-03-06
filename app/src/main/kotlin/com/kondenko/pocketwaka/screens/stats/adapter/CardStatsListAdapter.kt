package com.kondenko.pocketwaka.screens.stats.adapter

import android.content.Context
import android.util.TimeUtils
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.data.android.DateFormatter
import com.kondenko.pocketwaka.domain.stats.model.StatsItem
import com.kondenko.pocketwaka.ui.skeleton.Skeleton
import com.kondenko.pocketwaka.ui.skeleton.SkeletonAdapter
import com.kondenko.pocketwaka.utils.extensions.gone
import com.kondenko.pocketwaka.utils.extensions.invisible
import com.kondenko.pocketwaka.utils.extensions.limitWidthBy
import com.kondenko.pocketwaka.utils.extensions.secondsToHoursAndMinutes
import kotlinx.android.synthetic.main.item_stats_entity.view.*
import kotlin.math.roundToInt

class CardStatsListAdapter(
      context: Context,
      showSkeleton: Boolean,
      private val dateFormatter: DateFormatter
) : SkeletonAdapter<StatsItem, CardStatsListAdapter.ViewHolder>(context, showSkeleton) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflate(R.layout.item_stats_entity, parent)
        val skeleton = createSkeleton(view)
        return ViewHolder(view, skeleton)
    }

    override fun createSkeleton(view: View) = Skeleton(context, view).apply {
        onSkeletonShown { isShown ->
            view.progressbar_stats_item_percentage.isGone = isShown
        }
    }

    inner class ViewHolder(itemView: View, skeleton: Skeleton) : SkeletonViewHolder<StatsItem>(itemView, skeleton) {

        override fun bind(item: StatsItem) = with(itemView) {
            textview_stats_item_name.text = item.name
            textview_stats_item_name.limitWidthBy(textview_stats_item_percent, progressbar_stats_item_percentage.id)
            textview_stats_item_percent?.let {
                val (hours, minutes) = (item.totalSeconds?.toLong() ?: 0).secondsToHoursAndMinutes()
                if (!showSkeleton) {
                    it.text = dateFormatter.toHumanReadableTime(hours.toInt(), minutes.toInt(), DateFormatter.Format.Short)
                } else {
                    it.invisible()
                }
            }
            progressbar_stats_item_percentage.apply {
                color = item.color
                val percent = item.percent
                if (percent != null) progress = percent.roundToInt()
                else gone()
            }
            super.bind(item)
        }

    }

}