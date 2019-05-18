package com.kondenko.pocketwaka.screens.stats

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
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.domain.stats.model.StatsModel
import com.kondenko.pocketwaka.screens.base.BaseAdapter
import com.kondenko.pocketwaka.screens.base.BaseDiffCallback
import com.kondenko.pocketwaka.ui.Skeleton
import com.kondenko.pocketwaka.utils.component1
import com.kondenko.pocketwaka.utils.component2
import com.kondenko.pocketwaka.utils.extensions.adjustForDensity
import com.kondenko.pocketwaka.utils.extensions.setInvisible
import com.kondenko.pocketwaka.utils.negateIfTrue
import kotlinx.android.synthetic.main.layout_stats_best_day.view.*
import kotlinx.android.synthetic.main.layout_stats_card.view.*
import kotlinx.android.synthetic.main.layout_stats_info.view.*
import kotlin.math.roundToInt

class StatsAdapter(context: Context) : BaseAdapter<StatsModel, StatsAdapter.ViewHolder>(context) {

    private val typeInfo = 0

    private val typeBestDay = 1

    private val typeStats = 2

    var isSkeleton = false

    override var items: List<StatsModel> = super.items
        set(value) =
            value.filterNot { it is StatsModel.Metadata }.let {
                field = it
                super.items = it
            }

    override fun getItemViewType(position: Int): Int = when (items[position]) {
        is StatsModel.Info -> typeInfo
        is StatsModel.BestDay -> typeBestDay
        is StatsModel.Stats -> typeStats
        is StatsModel.Metadata -> throw IllegalArgumentException("StatsAdapter won't render metadata")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutId = when (viewType) {
            typeInfo -> R.layout.layout_stats_info
            typeBestDay -> R.layout.layout_stats_best_day
            typeStats -> R.layout.layout_stats_card
            else -> throw IllegalArgumentException("Unknown view type $viewType")
        }
        val view = inflate(layoutId, parent)
        return ViewHolder(view, setupSkeleton(view))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.skeleton) {
            if (isSkeleton) {
                super.onBindViewHolder(holder, position)
                refreshViews()
                show()
            } else {
                hide()
                super.onBindViewHolder(holder, position)
            }
        }
    }

    override fun getDiffCallback(oldList: List<StatsModel>, newList: List<StatsModel>): BaseDiffCallback<StatsModel> {
        return BaseDiffCallback(oldList, newList, areItemsTheSame = { a, b ->
            when (a) {
                is StatsModel.Info -> b is StatsModel.Info
                is StatsModel.Stats -> b is StatsModel.Stats
                is StatsModel.BestDay -> b is StatsModel.Stats
                else -> false
            }
        })
    }

    private fun setupSkeleton(view: View): Skeleton {
        fun Float.adjustValue(isSkeleton: Boolean) = (context.adjustForDensity(this)).negateIfTrue(!isSkeleton)

        val skeletonDrawable = context.getDrawable(R.drawable.all_skeleton_text)
                ?: ColorDrawable(Color.TRANSPARENT)
        // Move bestday_textview_time down a little bit so Best Day skeletons are evenly distributed
        val bestDayDateTransformation = { v: View, isSkeleton: Boolean ->
            when (v.id) {
                R.id.textview_bestday_time -> {
                    v.translationY += 3f.adjustValue(isSkeleton)
                }
                R.id.textview_stats_item -> {
                    v.translationX += 8f.adjustValue(isSkeleton)
                }
            }
        }
        return Skeleton(
                view as ViewGroup,
                skeletonBackground = skeletonDrawable,
                skeletonHeight = context.resources?.getDimension(R.dimen.height_all_skeleton_text)?.toInt()
                        ?: 16,
                transform = bestDayDateTransformation
        )
    }

    inner class ViewHolder(val view: View, val skeleton: Skeleton) : BaseViewHolder(view) {

        override fun bind(item: StatsModel) {
            when (item) {
                is StatsModel.Info -> view.renderInfo(item)
                is StatsModel.BestDay -> view.renderBestDay(item)
                is StatsModel.Stats -> view.renderStats(item)
            }
        }

        private fun View.renderInfo(model: StatsModel.Info) {
            textview_stats_time_total.text = model.humanReadableTotal.timeToSpannable()
            textview_stats_daily_average.text = model.humanReadableDailyAverage.timeToSpannable()
        }

        private fun View.renderBestDay(model: StatsModel.BestDay) {
            textview_bestday_date.text = model.date
            textview_bestday_time.text = model.time.timeToSpannable()
            imageview_bestday_illustration.isVisible = !isSkeleton
            val caption = context.getString(R.string.stats_caption_best_day, model.percentAboveAverage)
            if (model.percentAboveAverage > 0) textview_bestday_caption.text = caption
            else if (!isSkeleton) textview_bestday_caption.setInvisible()
        }

        private fun View.renderStats(model: StatsModel.Stats) {
            textview_stats_card_title.text = model.cardTitle
            view.statsCardRecyclerView.adapter = CardStatsListAdapter(context, model.items).also {
                it.isSkeleton = this@StatsAdapter.isSkeleton
            }
            view.statsCardRecyclerView.layoutManager = object : LinearLayoutManager(context) {
                override fun canScrollVertically() = false
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