package com.kondenko.pocketwaka.screens.summary

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
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
            val view = inflate(R.layout.item_summary_projects_title, parent)
            ProjectsTitleViewHolder(view)
        }
        ViewType.ProjectItem -> {
            val view = inflate(R.layout.item_summary_project, parent)
            ProjectsViewHolder(view, createSkeleton(view))
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

    // (secondary) TODO Fix jerky update animations
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
            view.textview_summary_project_name?.run {
                y += 4f.adjustValue(isSkeleton)
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
            if (item.percentDelta != null) {
                val isBelowAverage = (item.percentDelta ?: 0) < 0
                setupIcon(isBelowAverage)
                setupText(isBelowAverage, item.percentDelta)
            } else {
                forEach(imageview_summary_delta_icon, textview_summary_average_delta) {
                    it?.isGone = true
                }
            }
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

    inner class ProjectsTitleViewHolder(view: View) : ViewHolder<ProjectsTitle>(view, null)

    inner class ProjectsViewHolder(
          view: View,
          private val skeleton: Skeleton?
    ) : ViewHolder<ProjectItem>(view, skeleton) {

        private val branchesAdapter: BaseAdapter<ProjectInternalListItem, *> = provideBranchAdapter()

        override fun bind(item: ProjectItem) {
            bind(item, false)
        }

        fun bind(item: ProjectItem, onlyUpdateBranches: Boolean) {
            super.bind(item)
            val project = item.model
            branchesAdapter.items = project.branches.values.flatMap { branch ->
                val branchItem = if (showSkeleton) emptyList() else listOf(branch)
                branchItem + branch.commits.let {
                    if (project.isRepoConnected && it?.isEmpty() == true) listOf(NoCommitsLabel) else it
                          ?: emptyList()
                }
            }
            with(itemView) {
                recyclerview_summary_project_commits.showPadding(!project.branches.isNullOrEmpty())

                relativelayout_connect_repo.isVisible = !project.isRepoConnected

                if (onlyUpdateBranches) return

                textview_summary_project_name.text = project.name
                textview_summary_project_time.text = dateFormatter.secondsToHumanReadableTime(project.totalSeconds)
                textview_summary_project_name.limitWidthBy(textview_summary_project_time)

                val connectRepoButton = relativelayout_connect_repo.button_summary_project_connect_repo
                relativelayout_connect_repo.setOnClickListener {
                    connectRepoButton.performClick()
                }
                connectRepoButton.setOnClickListener {
                    connectRepoClicks.onNext(project.repositoryUrl)
                }

                recyclerview_summary_project_commits.adapter = branchesAdapter
            }
        }

        private fun RecyclerView.showPadding(show: Boolean) {
            val padding = if (show) {
                context.resources.getDimension(R.dimen.padding_summary_project_card_vertical)
            } else {
                0f
            }.roundToInt()
            updatePadding(bottom = padding)
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
            if (showSkeleton) {
                skeleton { view ->
                    Skeleton(context, view)
                }
            }
        }

    }

}