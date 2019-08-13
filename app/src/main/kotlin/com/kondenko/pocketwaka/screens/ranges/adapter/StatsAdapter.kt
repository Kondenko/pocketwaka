package com.kondenko.pocketwaka.screens.ranges.adapter

import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.domain.ranges.model.StatsUiModel
import com.kondenko.pocketwaka.screens.base.SkeletonAdapter
import com.kondenko.pocketwaka.ui.skeleton.Skeleton
import com.kondenko.pocketwaka.utils.SimpleCallback
import com.kondenko.pocketwaka.utils.extensions.component1
import com.kondenko.pocketwaka.utils.extensions.component2
import com.kondenko.pocketwaka.utils.extensions.setInvisible
import kotlinx.android.synthetic.main.item_status.view.*
import kotlinx.android.synthetic.main.layout_stats_best_day.view.*
import kotlinx.android.synthetic.main.layout_stats_card.view.*
import kotlinx.android.synthetic.main.layout_stats_info.view.*
import kotlin.math.roundToInt

class StatsAdapter(context: Context, showSkeleton: Boolean) : SkeletonAdapter<StatsUiModel, StatsAdapter.ViewHolder>(context, showSkeleton) {

    private enum class ViewType(val type: Int) {
        Status(0), Info(1), BestDay(2), Stats(3)
    }

    override var items: List<StatsUiModel> = super.items
        set(value) {
            field = value
            super.items = value
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
            ViewType.Info.type -> inflate(R.layout.layout_stats_info, parent)
            ViewType.BestDay.type -> inflate(R.layout.layout_stats_best_day, parent)
            ViewType.Stats.type -> inflate(R.layout.layout_stats_card, parent)
            else -> throw IllegalArgumentException("Unknown view type $viewType")
        }
        val skeleton = if (showSkeleton) createSkeleton(view) else null
        return ViewHolder(view, skeleton)
    }

    override fun getItemId(position: Int): Long = items[position].hashCode().toLong()

    override fun getDiffCallback(oldList: List<StatsUiModel>, newList: List<StatsUiModel>): DiffUtil.Callback {
        return SimpleCallback(
                oldList,
                newList,
                areItemsTheSame = { a, b ->
                    when (a) {
                        is StatsUiModel.Status -> b is StatsUiModel.Status
                        is StatsUiModel.Info -> b is StatsUiModel.Info
                        is StatsUiModel.BestDay -> b is StatsUiModel.BestDay
                        else -> false
                    }
                }
        )
    }

    override fun createSkeleton(view: View): Skeleton {
        val transformation = { v: View, isSkeleton: Boolean ->
            when (v.id) {
                R.id.textview_bestday_time -> {
                    v.translationY += 3f.adjustValue(isSkeleton)
                }
                R.id.textview_stats_card_title -> {
                    v.translationY += 8f.adjustValue(isSkeleton)
                }
            }
        }
        return Skeleton(
                context,
                view,
                transform = transformation
        )
    }

    inner class ViewHolder(val view: View, skeleton: Skeleton?) : SkeletonViewHolder(view, skeleton) {

        override fun bind(item: StatsUiModel) {
            when (item) {
                is StatsUiModel.Status -> view.render(item)
                is StatsUiModel.Info -> view.render(item)
                is StatsUiModel.BestDay -> view.render(item)
                is StatsUiModel.Stats -> view.render(item)
            }
        }

        private fun View.render(item: StatsUiModel.Status) {
            val isOffline = item is StatsUiModel.Status.Offline
            textview_status_description.setText(if (isOffline) R.string.status_offline else R.string.status_updating)
            imageView_status_offline.isInvisible = !isOffline
            progressbar_status_loading.isInvisible = isOffline
        }

        private fun View.render(item: StatsUiModel.Info) {
            textview_stats_time_total.text = item.humanReadableTotal.timeToSpannable()
            textview_stats_daily_average.text = item.humanReadableDailyAverage.timeToSpannable()
            super.bind(item)
        }

        private fun View.render(item: StatsUiModel.BestDay) {
            textview_bestday_date.text = item.date
            textview_bestday_time.text = item.time.timeToSpannable()
            imageview_bestday_illustration.isVisible = !showSkeleton
            val caption = context.getString(R.string.stats_caption_best_day, item.percentAboveAverage)
            if (item.percentAboveAverage > 0) textview_bestday_caption.text = caption
            else if (!showSkeleton) textview_bestday_caption.setInvisible()
            super.bind(item)
        }

        private fun View.render(item: StatsUiModel.Stats) {
            textview_stats_card_title.text = item.cardTitle
            with(view.statsCardRecyclerView) {
                layoutManager = object : LinearLayoutManager(context) {
                    override fun canScrollVertically() = false
                }
                adapter = CardStatsListAdapter(context, showSkeleton).apply {
                    items = item.items
                }
            }
            super.bind(item)
        }

        private fun String?.timeToSpannable(): Spannable? {
            if (this == null) return null
            val sb = SpannableStringBuilder(this)
            // Set spans for regular text
            sb.setSpan(
                    AbsoluteSizeSpan(context.resources.getDimension(R.dimen.textsize_stats_info_text).roundToInt()),
                    0,
                    length,
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            )
            sb.setSpan(
                    ForegroundColorSpan(ContextCompat.getColor(context, R.color.color_text_black_secondary)),
                    0,
                    length,
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            )
            // Find start and end indices of numbers
            val numberRegex = "\\d+".toRegex()
            val numberIndices = numberRegex.findAll(this).map { it.range }
            // Highlight numbers with spans
            for ((from, to) in numberIndices) {
                val toActual = to + 1
                sb.setSpan(
                        AbsoluteSizeSpan(context.resources.getDimension(R.dimen.textsize_stats_info_number).roundToInt()),
                        from,
                        toActual,
                        Spanned.SPAN_INCLUSIVE_EXCLUSIVE
                )
                sb.setSpan(
                        ForegroundColorSpan(ContextCompat.getColor(context, R.color.color_text_black_primary)),
                        from,
                        toActual,
                        Spanned.SPAN_INCLUSIVE_EXCLUSIVE
                )
            }
            return sb
        }

    }

}