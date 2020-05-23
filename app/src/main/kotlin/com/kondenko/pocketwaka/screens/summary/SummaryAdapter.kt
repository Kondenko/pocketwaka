package com.kondenko.pocketwaka.screens.summary

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.recyclerview.widget.DiffUtil
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.data.android.DateFormatter
import com.kondenko.pocketwaka.domain.summary.model.*
import com.kondenko.pocketwaka.domain.summary.model.SummaryUiModel.*
import com.kondenko.pocketwaka.screens.base.BaseAdapter
import com.kondenko.pocketwaka.screens.renderStatus
import com.kondenko.pocketwaka.ui.skeleton.Skeleton
import com.kondenko.pocketwaka.ui.skeleton.SkeletonAdapter
import com.kondenko.pocketwaka.utils.createAdapter
import com.kondenko.pocketwaka.utils.diffutil.SimpleCallback
import com.kondenko.pocketwaka.utils.exceptions.IllegalViewTypeException
import com.kondenko.pocketwaka.utils.extensions.findInstance
import com.kondenko.pocketwaka.utils.extensions.forEach
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

class SummaryAdapter(
      context: Context,
      showSkeleton: Boolean,
      private val timeSpannableCreator: SpannableCreator,
      private val dateFormatter: DateFormatter
) : SkeletonAdapter<SummaryUiModel, SummaryAdapter.ViewHolder<SummaryUiModel>>(context, showSkeleton) {

    sealed class Payload {
        object ProjectChanged : Payload()
    }

    private enum class ViewType(val type: Int) {
        Status(0),
        TimeTracked(1),
        ProjectsTitle(2),
        ProjectItem(3)
    }

    private val connectRepoClicks = PublishSubject.create<String>()

    fun connectRepoClicks(): Observable<String> = connectRepoClicks

    override fun getItemViewType(position: Int): Int = when (items[position]) {
        is Status -> ViewType.Status
        is TimeTracked -> ViewType.TimeTracked
        is ProjectsTitle -> ViewType.ProjectsTitle
        is ProjectItem -> ViewType.ProjectItem
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
        ViewType.ProjectItem -> {
            val view = inflate(R.layout.item_summary_project, parent)
            ProjectsViewHolder(view, provideBranchAdapter(), createSkeleton(view))
        }
        else -> throw IllegalViewTypeException()
    }

    override fun onBindViewHolder(holder: ViewHolder<SummaryUiModel>, position: Int, payloads: List<Any>) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            val projectPayload = payloads.findInstance<Payload.ProjectChanged>()
            if (projectPayload != null) {
                (holder as? ProjectsViewHolder)?.bind(items[position] as ProjectItem, onlyUpdateBranches = true)
            }
        }
    }

    // TODO Fix jerky update animations
    override fun getDiffCallback(oldList: List<SummaryUiModel>, newList: List<SummaryUiModel>): DiffUtil.Callback {
        return SimpleCallback(
              oldList, newList,
              areItemsTheSame = { a, b ->
                  val areProjectsTheSame = a is ProjectItem && b is ProjectItem && a.model.name == b.model.name
                  areProjectsTheSame
              },
              getChangePayload = { a, b ->
                  Payload.ProjectChanged
              }
        )
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

    private inner class StatusViewHolder(private val view: View) : ViewHolder<Status>(view, null) {
        override fun bind(item: Status) = view.renderStatus(item.status)
    }

    inner class TimeTrackedViewHolder(view: View, skeleton: Skeleton) : ViewHolder<TimeTracked>(view, skeleton) {

        override fun bind(item: TimeTracked) = with(itemView) {
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

    inner class ProjectsTitleViewHolder(view: View) : ViewHolder<ProjectsTitle>(view, null)

    inner class ProjectsViewHolder(
          view: View,
          private val branchesAdapter: BaseAdapter<ProjectInternalListItem, *>,
          private val skeleton: Skeleton?
    ) : ViewHolder<ProjectItem>(view, skeleton) {

        override fun bind(item: ProjectItem) {
            bind(item, false)
        }

        fun bind(item: ProjectItem, onlyUpdateBranches: Boolean) {
            super.bind(item)
            val project = item.model
            branchesAdapter.items = project.branches.values.flatMap { branch ->
                listOf(branch) + branch.commits.let {
                    if (project.isRepoConnected && it?.isEmpty() == true) listOf(NoCommitsLabel) else it ?: emptyList()
                }
            }
            if (onlyUpdateBranches) return
            with(itemView) {
                textview_summary_project_name.text = project.name
                textview_summary_project_time.text = dateFormatter.secondsToHumanReadableTime(project.totalSeconds)
                textview_summary_project_name.limitWidthBy(textview_summary_project_time)
                project.repositoryUrl?.let { repoUrl ->
                    setOnClickListener {
                        button_summary_project_connect_repo.performClick()
                    }
                    button_summary_project_connect_repo.setOnClickListener {
                        connectRepoClicks.onNext(repoUrl)
                    }
                }
                      ?: forEach(button_summary_project_connect_repo, button_summary_project_connect_repo) {
                          it?.isGone = true
                      }
                recyclerview_summary_project_commits.adapter = branchesAdapter
/*
                TODO Make sure it works after bringing back skeletons in FragmenSummary
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
*/
            }
        }

    }

    private fun provideBranchAdapter() = createAdapter<ProjectInternalListItem>(context) {
        viewHolder<Branch>(R.layout.item_summary_project_branch) { item, _ ->
            textview_summary_project_branch.text = item.name
            textview_summary_project_branch_time.text = dateFormatter.secondsToHumanReadableTime(item.totalSeconds)
            textview_summary_project_branch.limitWidthBy(textview_summary_project_branch_time)
        }
        viewHolder<Commit>(R.layout.item_summary_project_commit) { item, _ ->
            textview_summary_project_commit_message.text = item.message
            textview_summary_project_commit_time.text = dateFormatter.secondsToHumanReadableTime(item.totalSeconds)
            textview_summary_project_commit_message.limitWidthBy(textview_summary_project_commit_time)
        }
        viewHolder<NoCommitsLabel>(R.layout.item_summary_project_no_commits)
    }

}