package com.kondenko.pocketwaka.screens.summary

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.domain.summary.model.ProjectModel
import com.kondenko.pocketwaka.domain.summary.model.SummaryUiModel
import com.kondenko.pocketwaka.screens.renderStatus
import com.kondenko.pocketwaka.ui.skeleton.Skeleton
import com.kondenko.pocketwaka.ui.skeleton.SkeletonAdapter
import com.kondenko.pocketwaka.utils.createAdapter
import com.kondenko.pocketwaka.utils.exceptions.IllegalViewTypeException
import com.kondenko.pocketwaka.utils.extensions.limitWidthBy
import com.kondenko.pocketwaka.utils.spannable.SpannableCreator
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.item_summary_project.view.*
import kotlinx.android.synthetic.main.item_summary_project_branch.view.*
import kotlinx.android.synthetic.main.item_summary_project_commit.view.*
import kotlinx.android.synthetic.main.item_summary_project_connect_repo.view.*
import kotlinx.android.synthetic.main.item_summary_project_name.view.*
import kotlinx.android.synthetic.main.item_summary_time_tracked.view.*
import kotlin.math.abs
import kotlin.math.roundToInt

class SummaryAdapter(context: Context, showSkeleton: Boolean, private val timeSpannableCreator: SpannableCreator)
    : SkeletonAdapter<SummaryUiModel, SummaryAdapter.ViewHolder<SummaryUiModel>>(context, showSkeleton) {

    private enum class ViewType(val type: Int) {
        Status(0),
        TimeTracked(1),
        ProjectsTitle(2),
        Projects(3)
    }

    private val connectRepoClicks = PublishSubject.create<String>()

    fun connectRepoClicks(): Observable<String> = connectRepoClicks

    override fun getItemViewType(position: Int): Int = when (items[position]) {
        is SummaryUiModel.Status -> ViewType.Status
        is SummaryUiModel.TimeTracked -> ViewType.TimeTracked
        is SummaryUiModel.ProjectsTitle -> ViewType.ProjectsTitle
        is SummaryUiModel.Project -> ViewType.Projects
    }.type

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (ViewType.values().getOrNull(viewType)) {
        ViewType.Status -> StatusViewHolder(inflate(R.layout.item_status, parent))
        ViewType.TimeTracked -> {
            val view = inflate(R.layout.item_summary_time_tracked, parent)
            TimeTrackedViewHolder(view, createSkeleton(view))
        }
        ViewType.ProjectsTitle -> {
            ProjectsTitleViewHolder(inflate(R.layout.item_summary_projects_title, parent))
        }
        ViewType.Projects -> {
            val view = inflate(R.layout.item_summary_project, parent)
            ProjectsViewHolder(view, createSkeleton(view))
        }
        else -> throw IllegalViewTypeException()
    }

    override fun createSkeleton(view: View) = Skeleton(context, view).apply {
        onSkeletonShown { isSkeleton: Boolean ->
            view.textview_summary_time?.run {
                y += 16f.adjustValue(isSkeleton).roundToInt()
            }
            view.linearlayout_delta_container?.run {
                y += 32f.adjustValue(isSkeleton).roundToInt()
            }
        }
    }

    abstract inner class ViewHolder<out T : SummaryUiModel>(view: View, skeleton: Skeleton?)
        : SkeletonViewHolder<T>(view, skeleton)

    private inner class StatusViewHolder(private val view: View) : ViewHolder<SummaryUiModel.Status>(view, null) {
        override fun bind(item: SummaryUiModel.Status) = view.renderStatus(item.status)
    }

    inner class TimeTrackedViewHolder(view: View, skeleton: Skeleton) : ViewHolder<SummaryUiModel.TimeTracked>(view, skeleton) {

        override fun bind(item: SummaryUiModel.TimeTracked) = with(itemView) {
            val isBelowAverage = (item.percentDelta ?: 0) < 0
            setupIcon(isBelowAverage)
            setupText(isBelowAverage, item.percentDelta)
            if (!showSkeleton) {
                textview_summary_time.text = timeSpannableCreator.create(item.time, R.dimen.textsize_summary_number, R.dimen.textsize_summary_text)
            }
            super.bind(item)
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

        private fun View.setupText(isBelowAverage: Boolean, delta: Int?) = with(textview_summary_average_delta) {
            if (delta != null) {
                val directionRes = if (isBelowAverage) {
                    R.string.summary_template_average_delta_below
                } else {
                    R.string.summary_template_average_delta_above
                }
                val deltaText = context.getString(directionRes)
                text = context.getString(R.string.summary_template_average_delta, abs(delta), deltaText)
            } else {
                isGone = true
            }
        }

        private fun getDeltaIcon(isBelowAverage: Boolean): Drawable? =
              context.getDrawable(R.drawable.ic_arrow_down)?.apply {
                  val color = if (isBelowAverage) R.color.color_descent else R.color.color_growth
                  setTint(ContextCompat.getColor(context, color))
              }

    }

    inner class ProjectsTitleViewHolder(view: View) : ViewHolder<SummaryUiModel.ProjectsTitle>(view, null)

    inner class ProjectsViewHolder(view: View, skeleton: Skeleton?) : ViewHolder<SummaryUiModel.Project>(view, skeleton) {

        override fun bind(item: SummaryUiModel.Project) {
            super.bind(item)
            itemView.recyclerview_summary_project.adapter = createAdapter<ProjectModel>(context) {
                items { item.models }
                if (showSkeleton) {
                    skeleton { view ->
                        Skeleton(context, view).apply {
                            onSkeletonShown { isSkeleton ->
                                view.textview_summary_project_name?.run {
                                    y += 4f.adjustValue(isSkeleton)
                                }
                            }
                        }
                    }
                }
                viewHolder<ProjectModel.ProjectName>(R.layout.item_summary_project_name) { item, _ ->
                    textview_summary_project_name.text = item.name
                    textview_summary_project_time.text = item.timeTracked
                    textview_summary_project_name limitWidthBy textview_summary_project_time
                }
                viewHolder<ProjectModel.Branch>(R.layout.item_summary_project_branch) { item, _ ->
                    textview_summary_project_branch.text = item.name
                    textview_summary_project_branch_time.text = item.timeTracked
                    textview_summary_project_branch limitWidthBy textview_summary_project_branch_time
                }
                viewHolder<ProjectModel.Commit>(R.layout.item_summary_project_commit) { item, _ ->
                    textview_summary_project_commit_message.text = item.message
                    textview_summary_project_commit_time.text = item.timeTracked
                    textview_summary_project_commit_message limitWidthBy textview_summary_project_commit_time
                }
                viewHolder<ProjectModel.ConnectRepoAction>(R.layout.item_summary_project_connect_repo) { item, _ ->
                    button_summary_project_connect_repo.setOnClickListener {
                        connectRepoClicks.onNext(item.url)
                    }
                }
                viewHolder<ProjectModel.NoCommitsLabel>(R.layout.item_summary_project_no_commits)
            }
        }

    }

}