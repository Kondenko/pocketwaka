package com.kondenko.pocketwaka.screens.ranges.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.domain.ranges.model.StatsUiModel
import com.kondenko.pocketwaka.screens.ScreenStatus
import com.kondenko.pocketwaka.screens.base.SkeletonAdapter
import com.kondenko.pocketwaka.ui.skeleton.Skeleton
import com.kondenko.pocketwaka.utils.diffutil.SimpleCallback
import com.kondenko.pocketwaka.utils.extensions.setInvisible
import com.kondenko.pocketwaka.utils.spannable.SpannableCreator
import kotlinx.android.synthetic.main.item_all_entities_card.view.*
import kotlinx.android.synthetic.main.item_stats_best_day.view.*
import kotlinx.android.synthetic.main.item_stats_info.view.*
import kotlinx.android.synthetic.main.item_status.view.*

class StatsAdapter(
        context: Context,
        showSkeleton: Boolean,
        private val timeSpannableCreator: SpannableCreator
) : SkeletonAdapter<StatsUiModel, StatsAdapter.ViewHolder>(context, showSkeleton) {

    private enum class ViewType(val type: Int) {
        Status(0), Info(1), BestDay(2), Stats(3)
    }

    init {
        setHasStableIds(true)
    }

    override fun getItemViewType(position: Int): Int = when (items[position]) {
        is StatsUiModel.Status -> ViewType.Status
        is StatsUiModel.Info -> ViewType.Info
        is StatsUiModel.BestDay -> ViewType.BestDay
        is StatsUiModel.Stats -> ViewType.Stats
    }.type

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = when (viewType) {
            ViewType.Status.type -> inflate(R.layout.item_status, parent)
            ViewType.Info.type -> inflate(R.layout.item_stats_info, parent)
            ViewType.BestDay.type -> inflate(R.layout.item_stats_best_day, parent)
            ViewType.Stats.type -> inflate(R.layout.item_all_entities_card, parent)
            else -> throw IllegalArgumentException("Unknown view type $viewType")
        }
        return ViewHolder(view, createSkeleton(view))
    }

    override fun getItemId(position: Int): Long = items[position].hashCode().toLong()

    override fun getDiffCallback(oldList: List<StatsUiModel>, newList: List<StatsUiModel>): DiffUtil.Callback {
        return SimpleCallback(
                oldList,
                newList,
                areItemsTheSame = { a, b -> a::class == b::class }
        )
    }

    override fun createSkeleton(view: View) = Skeleton(context, view).apply {
        onSkeletonShown { isSkeleton: Boolean ->
            view.textview_bestday_time?.run { translationY += 3f.adjustValue(isSkeleton) }
            view.textview_all_entities_card_title?.run { translationY += 8f.adjustValue(isSkeleton) }
        }
    }

    inner class ViewHolder(val view: View, skeleton: Skeleton) : SkeletonViewHolder<StatsUiModel>(view, skeleton) {

        override fun bind(item: StatsUiModel) {
            when (item) {
                is StatsUiModel.Status -> view.render(item)
                is StatsUiModel.Info -> view.render(item)
                is StatsUiModel.BestDay -> view.render(item)
                is StatsUiModel.Stats -> view.render(item)
            }
        }

        private fun View.render(item: StatsUiModel.Status) {
            val isOffline = item.status is ScreenStatus.Offline
            textview_status_description.setText(if (isOffline) R.string.status_offline else R.string.status_updating)
            imageView_status_offline.isInvisible = !isOffline
            progressbar_status_loading.isInvisible = isOffline
        }

        private fun View.render(item: StatsUiModel.Info) {
            textview_stats_time_total.text = timeSpannableCreator.create(item.humanReadableTotal)
            textview_stats_daily_average.text = timeSpannableCreator.create(item.humanReadableDailyAverage)
            super.bind(item)
        }

        private fun View.render(item: StatsUiModel.BestDay) {
            textview_bestday_date.text = item.date
            textview_bestday_time.text = timeSpannableCreator.create(item.time)
            imageview_bestday_illustration.isVisible = !showSkeleton
            val caption = context.getString(R.string.stats_caption_best_day, item.percentAboveAverage)
            if (item.percentAboveAverage > 0) textview_bestday_caption.text = caption
            else if (!showSkeleton) textview_bestday_caption.setInvisible()
            super.bind(item)
        }

        private fun View.render(item: StatsUiModel.Stats) {
            textview_all_entities_card_title.text = item.cardTitle
            with(view.recyclerview_all_entitites) {
                layoutManager = object : LinearLayoutManager(context) {
                    override fun canScrollVertically() = false
                }
                adapter = CardStatsListAdapter(context, showSkeleton).apply {
                    items = item.items
                }
            }
            super.bind(item)
        }

    }

}