package com.kondenko.pocketwaka.screens.summary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.analytics.Event
import com.kondenko.pocketwaka.analytics.EventTracker
import com.kondenko.pocketwaka.screens.Refreshable
import com.kondenko.pocketwaka.utils.diffutil.diffUtil
import com.kondenko.pocketwaka.utils.extensions.observe
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_summary_container.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentSummaryContainer : Fragment(), Refreshable {

    private val vm: SummaryRangeViewModel by viewModel()

    private val eventTracker: EventTracker by inject()

    private lateinit var pagesAdapter: SummaryContainerAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_summary_container, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pagesAdapter = SummaryContainerAdapter(this)
        viewpager_summary_container.adapter = pagesAdapter
        vm.dateChanges().observe(viewLifecycleOwner) {
            pagesAdapter.summaryDates = listOf(it)
        }
    }

    override fun subscribeToRefreshEvents(refreshEvents: Observable<Unit>): Disposable {
        return refreshEvents.subscribe {
            eventTracker.log(Event.ManualUpdate)
            // TODO Update selected Fragment using ViewModel
        }
    }

    private class SummaryContainerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

        var summaryDates: List<SummaryDate> by diffUtil<SummaryDate, SummaryContainerAdapter>()

        override fun getItemCount(): Int = summaryDates.size

        override fun createFragment(position: Int): Fragment = FragmentSummary.create(summaryDates[position])

    }

}
