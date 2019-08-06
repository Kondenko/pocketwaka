package com.kondenko.pocketwaka.screens.ranges.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.doOnNextLayout
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.domain.stats.model.StatsModel
import com.kondenko.pocketwaka.screens.base.BaseAdapter
import com.kondenko.pocketwaka.ui.Skeleton
import com.kondenko.pocketwaka.utils.SimpleCallback
import com.kondenko.pocketwaka.utils.extensions.*
import kotlinx.android.synthetic.main.item_status.view.*
import kotlinx.android.synthetic.main.layout_stats_best_day.view.*
import kotlinx.android.synthetic.main.layout_stats_card.view.*
import kotlinx.android.synthetic.main.layout_stats_info.view.*
import kotlin.math.roundToInt

class StatsAdapter(context: Context, private val isSkeleton: Boolean = false) : BaseAdapter<StatsModel, StatsAdapter.ViewHolder>(context) {

    private enum class ViewType(val type: Int) {
        Status(0), Info(1), BestDay(2), Stats(3)
    }

    override var items: List<StatsModel> = super.items
        set(value) {
            field = value
            super.items = value
        }

    init {
        setHasStableIds(true)
    }

    override fun getItemViewType(position: Int): Int = when (items[position]) {
        is StatsModel.Status -> ViewType.Status
        is StatsModel.Info -> ViewType.Info
        is StatsModel.BestDay -> ViewType.BestDay
        is StatsModel.Stats -> ViewType.Stats
    }.type

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View
        val skeleton: Skeleton?
        when (viewType) {
            ViewType.Status.type -> {
                view = inflate(R.layout.item_status, parent)
                skeleton = if (isSkeleton && view is ViewGroup) createSkeleton(view) else null
                ViewHolder(view, skeleton)
            }
            ViewType.Info.type -> {
                view = inflate(R.layout.layout_stats_info, parent)
                skeleton = if (isSkeleton && view is ViewGroup) createSkeleton(view) else null
                ViewHolder(view, skeleton)
            }
            ViewType.BestDay.type -> {
                view = inflate(R.layout.layout_stats_best_day, parent)
                skeleton = if (isSkeleton && view is ViewGroup) createSkeleton(view) else null
                ViewHolder(view, skeleton)
            }
            ViewType.Stats.type -> {
                view = inflate(R.layout.layout_stats_card, parent)
                skeleton = if (isSkeleton && view is ViewGroup) createSkeleton(view) else null
            }
            else -> {
                throw IllegalArgumentException("Unknown view type $viewType")
            }
        }
        return ViewHolder(view, skeleton)
    }

    override fun getItemId(position: Int): Long = items[position].hashCode().toLong()

    override fun getDiffCallback(oldList: List<StatsModel>, newList: List<StatsModel>): DiffUtil.Callback {
        return SimpleCallback(oldList, newList, areItemsTheSame = { a, b ->
            when (a) {
                is StatsModel.Status -> b is StatsModel.Status
                is StatsModel.Info -> b is StatsModel.Info
                is StatsModel.BestDay -> b is StatsModel.BestDay
                else -> false
            }
        })
    }

    private fun createSkeleton(view: ViewGroup): Skeleton {
        fun Float.adjustValue(isSkeleton: Boolean) = (context.adjustForDensity(this)).negateIfTrue(!isSkeleton)
        val skeletonDrawable = context.getDrawable(R.drawable.all_skeleton_text)
                ?: ColorDrawable(Color.TRANSPARENT)
        // Move bestday_textview_time down a little bit so Best Day skeletons are evenly distributed
        val bestDayDateTransformation = { v: View, isSkeleton: Boolean ->
            when (v.id) {
                R.id.textview_bestday_time -> {
                    v.translationY += 3f.adjustValue(isSkeleton)
                }
                R.id.textview_stats_item_name -> {
                    v.translationX += 8f.adjustValue(isSkeleton)
                }
                R.id.textview_stats_card_title -> {
                    v.translationY += 8f.adjustValue(isSkeleton)
                }
            }
        }
        val skeletonHeight = context.resources?.getDimension(R.dimen.height_all_skeleton_text)?.toInt()
        return Skeleton(
                view,
                skeletonBackground = skeletonDrawable,
                skeletonHeight = skeletonHeight ?: 16,
                transform = bestDayDateTransformation
        )
    }

    inner class ViewHolder(val view: View, private val skeleton: Skeleton?) : BaseViewHolder(view) {

        override fun bind(item: StatsModel) {
            when (item) {
                is StatsModel.Status -> view.render(item)
                is StatsModel.Info -> view.render(item)
                is StatsModel.BestDay -> view.render(item)
                is StatsModel.Stats -> view.render(item)
            }
        }

        private fun View.render(item: StatsModel.Status) {
            val isOffline = item is StatsModel.Status.Offline
            textview_status_description.setText(if (isOffline) R.string.status_offline else R.string.status_updating)
            imageView_status_offline.isInvisible = !isOffline
            progressbar_status_loading.isInvisible = isOffline
        }

        private fun View.render(item: StatsModel.Info) {
            textview_stats_time_total.text = item.humanReadableTotal.timeToSpannable()
            textview_stats_daily_average.text = item.humanReadableDailyAverage.timeToSpannable()
            post {
                skeleton?.show()
            }
        }

        private fun View.render(item: StatsModel.BestDay) {
            textview_bestday_date.text = item.date
            textview_bestday_time.text = item.time.timeToSpannable()
            imageview_bestday_illustration.isVisible = !isSkeleton
            val caption = context.getString(R.string.stats_caption_best_day, item.percentAboveAverage)
            if (item.percentAboveAverage > 0) textview_bestday_caption.text = caption
            else if (!isSkeleton) textview_bestday_caption.setInvisible()
            post {
                skeleton?.show()
            }
        }

        private fun View.render(item: StatsModel.Stats) {
            textview_stats_card_title.text = item.cardTitle
            with(view.statsCardRecyclerView) {
                layoutManager = object : LinearLayoutManager(context) {
                    override fun canScrollVertically() = false
                }
                doOnNextLayout {
                    skeleton?.addViews(this@render as ViewGroup)
                    skeleton?.show()
                }
                adapter = CardStatsListAdapter(context, item.items, isSkeleton)
            }
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