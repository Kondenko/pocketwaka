package com.kondenko.pocketwaka.screens.daily

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.domain.daily.model.SummaryUiModel
import com.kondenko.pocketwaka.screens.State
import com.kondenko.pocketwaka.ui.skeleton.RecyclerViewSkeleton
import com.kondenko.pocketwaka.utils.extensions.observe
import com.kondenko.pocketwaka.utils.extensions.report
import kotlinx.android.synthetic.main.fragment_summary.view.*
import org.koin.androidx.scope.currentScope
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import timber.log.Timber

class FragmentSummary : Fragment() {

    private val vm: SummaryViewModel by viewModel()

    private lateinit var recyclerSkeleton: RecyclerViewSkeleton<SummaryUiModel, SummaryAdapter>

    private val skeletonItems = listOf(
            SummaryUiModel.TimeTracked("", 1)
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_summary, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupList(view)
        vm.state.observe(viewLifecycleOwner) { it.render() }
    }

    private fun State<List<SummaryUiModel>>.render() {
        Timber.d("New summary state: $this")
        recyclerSkeleton.show((this as? State.Loading)?.isInterrupting == true)
        when (this) {
            is State.Success -> {
                // TODO Remove filtering
                recyclerSkeleton.actualAdapter.items = data.filterIsInstance<SummaryUiModel.TimeTracked>()
            }
            is State.Loading -> {
            }
            is State.Failure -> exception?.report()
        }
    }

    private fun setupList(view: View) {
        with(view.recyclerview_summary) {
            recyclerSkeleton = currentScope.get { parametersOf(this, skeletonItems) }
            adapter = recyclerSkeleton.actualAdapter
        }
    }

}
