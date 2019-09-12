package com.kondenko.pocketwaka.screens.daily

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.domain.daily.model.SummaryUiModel
import com.kondenko.pocketwaka.screens.base.SkeletonAdapter
import com.kondenko.pocketwaka.ui.skeleton.Skeleton
import com.kondenko.pocketwaka.utils.exceptions.IllegalViewTypeException
import com.kondenko.pocketwaka.utils.extensions.setViewsVisibility
import com.kondenko.pocketwaka.utils.spannable.SpannableCreator
import kotlinx.android.synthetic.main.item_summary_time_tracked.view.*
import kotlin.math.abs
import kotlin.math.roundToInt

class SummaryAdapter(context: Context, showSkeleton: Boolean, private val timeSpannableCreator: SpannableCreator)
    : SkeletonAdapter<SummaryUiModel, SummaryAdapter.SummaryViewHolder<SummaryUiModel>>(context, showSkeleton) {

    private enum class ViewType(val type: Int) {
        Status(0),
        TimeTracked(1),
        ProjectsTitle(2),
        Projects(3)
    }

    override fun getItemViewType(position: Int): Int = when (items[position]) {
        is SummaryUiModel.Status.Offline -> ViewType.Status
        is SummaryUiModel.TimeTracked -> ViewType.TimeTracked
        is SummaryUiModel.ProjectsTitle -> ViewType.ProjectsTitle
        is SummaryUiModel.Projects -> ViewType.Projects
        else -> throw IllegalViewTypeException()
    }.type

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (ViewType.values().getOrNull(viewType)) {
        ViewType.Status -> TODO()
        ViewType.TimeTracked -> {
            val view = inflate(R.layout.item_summary_time_tracked, parent)
            TimeTrackedViewHolder(view, createSkeleton(view))
        }
        ViewType.ProjectsTitle -> {
            ProjectsTitleViewHolder(inflate(R.layout.item_summary_projects_title, parent))
        }
        ViewType.Projects -> {
            val view = inflate(R.layout.item_all_entities_card, parent)
            ProjectsViewHolder(view, createSkeleton(view))
        }
        else -> throw IllegalArgumentException()
    }

    override fun createSkeleton(view: View) = Skeleton(context, view).apply {
        onSkeletonShown { isSkeleton: Boolean ->
            view.textview_summary_time.y += 8f.adjustValue(isSkeleton).roundToInt()
            view.linearlayout_delta_container.y += 16f.adjustValue(isSkeleton).roundToInt()
        }
    }

    abstract inner class SummaryViewHolder<out T : SummaryUiModel>(view: View, skeleton: Skeleton?)
        : SkeletonViewHolder<T>(view, skeleton)

    inner class TimeTrackedViewHolder(view: View, skeleton: Skeleton?) : SummaryViewHolder<SummaryUiModel.TimeTracked>(view, skeleton) {

        override fun bind(item: SummaryUiModel.TimeTracked) {
            with(itemView) {
                val isBelowAverage = item.percentDelta < 0
                if (item.percentDelta != 0) {
                    setupIcon(isBelowAverage)
                    setupText(isBelowAverage, item.percentDelta)
                } else {
                    setViewsVisibility(View.GONE, textview_summary_average_delta, imageview_summary_delta_icon)
                }
                textview_summary_time.text = timeSpannableCreator.create(item.time)
                super.bind(item)
            }
        }

        private fun View.setupIcon(isBelowAverage: Boolean) = with(imageview_summary_delta_icon) {
            val icon = getDeltaIcon(isBelowAverage)
            if (icon != null) {
                setImageDrawable(icon)
                if (!isBelowAverage) rotation = 180f
            } else {
                isGone = true
            }
        }

        private fun View.setupText(isBelowAverage: Boolean, delta: Int) = with(textview_summary_average_delta) {
            val directionRes = if (isBelowAverage) {
                R.string.summary_template_average_delta_below
            } else {
                R.string.summary_template_average_delta_above
            }
            val deltaText = context.getString(directionRes)
            text = context.getString(R.string.summary_template_average_delta, abs(delta), deltaText)
        }

        private fun getDeltaIcon(isBelowAverage: Boolean): Drawable? =
                context.getDrawable(R.drawable.ic_arrow_down)?.apply {
                    val color = if (isBelowAverage) R.color.color_descent else R.color.color_growth
                    setTint(ContextCompat.getColor(context, color))
                }

    }

    inner class ProjectsTitleViewHolder(view: View) : SummaryViewHolder<SummaryUiModel.ProjectsTitle>(view, null)

    inner class ProjectsViewHolder(view: View, skeleton: Skeleton?) : SummaryViewHolder<SummaryUiModel.Projects>(view, skeleton) {

    }

}