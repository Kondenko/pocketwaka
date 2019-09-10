package com.kondenko.pocketwaka.screens.daily

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.domain.daily.model.SummaryUiModel
import com.kondenko.pocketwaka.screens.base.SkeletonAdapter
import com.kondenko.pocketwaka.ui.skeleton.Skeleton
import com.kondenko.pocketwaka.utils.exceptions.IllegalViewTypeException
import kotlinx.android.synthetic.main.item_summary_time_tracked.view.*

class SummaryAdapter(context: Context) : SkeletonAdapter<SummaryUiModel, SummaryAdapter.SummaryViewHolder<SummaryUiModel>>(context) {

    private enum class ViewType(val type: Int) {
        Status(0),
        TimeTracked(1),
        ProjectsSubtitle(2),
        Projects(3)
    }

    override fun getItemViewType(position: Int): Int = when(items[position]) {
        is SummaryUiModel.Status.Offline -> ViewType.Status.type
        is SummaryUiModel.TimeTracked -> ViewType.TimeTracked.type
        is SummaryUiModel.ProjectsSubtitle -> ViewType.ProjectsSubtitle.type
        is SummaryUiModel.Projects -> ViewType.Projects.type
        else -> throw IllegalViewTypeException()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SummaryViewHolder<SummaryUiModel> =
            when(ViewType.values().getOrNull(viewType)) {
                ViewType.Status -> TODO()
                ViewType.TimeTracked -> TimeTrackedViewHolder(inflate(R.layout.item_summary_time_tracked, parent), null)
                ViewType.ProjectsSubtitle -> TODO()
                ViewType.Projects -> TODO()
                else -> throw IllegalArgumentException()
            }

    override fun createSkeleton(view: View): Skeleton {
        return Skeleton(context)
    }

    abstract inner class SummaryViewHolder<out T : SummaryUiModel>(view: View, skeleton: Skeleton?)
        : SkeletonViewHolder<T>(view, skeleton)

    inner class TimeTrackedViewHolder(view: View, skeleton: Skeleton?) : SummaryViewHolder<SummaryUiModel.TimeTracked>(view, skeleton) {
        override fun bind(item: SummaryUiModel.TimeTracked) {
            itemView.textview_summary_time.text = item.time
        }
    }

}