package com.kondenko.pocketwaka.screens.daily

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.domain.daily.model.SummaryUiModel
import com.kondenko.pocketwaka.screens.State
import com.kondenko.pocketwaka.utils.extensions.observe
import com.kondenko.pocketwaka.utils.extensions.report
import kotlinx.android.synthetic.main.fragment_summary.view.*
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import timber.log.Timber

class FragmentSummary : Fragment() {

    private val vm: SummaryViewModel by viewModel()

    private lateinit var summaryAdapter: SummaryAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_summary, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupList(view, view.context)
        vm.state.observe(viewLifecycleOwner) {
            Timber.d("New summary state: $it")
            when (it) {
                is State.Success -> summaryAdapter.items = it.data.filterIsInstance<SummaryUiModel.TimeTracked>() // TODO Remove filtering
                is State.Failure -> it.exception?.report()
            }
        }
    }

    private fun setupList(view: View, context: Context) {
        summaryAdapter = get { parametersOf(context, false) }
        with(view.recyclerview_summary) {
            adapter = summaryAdapter
        }
    }

}
