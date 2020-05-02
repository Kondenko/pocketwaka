package com.kondenko.pocketwaka.screens.summary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.analytics.Event
import com.kondenko.pocketwaka.analytics.EventTracker
import com.kondenko.pocketwaka.screens.Refreshable
import com.kondenko.pocketwaka.utils.WakaLog
import com.kondenko.pocketwaka.utils.date.DateRange
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

    private lateinit var pagerAdapter: SummaryContainerAdapter

    private val onPageChanged = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            val day = pagerAdapter.summaryDates[position] as? DateRange.SingleDay
            day?.let(vm::onDateScreenOpen)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_summary_container, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pagerAdapter = SummaryContainerAdapter(this)
        with(viewpager_summary_container) {
            registerOnPageChangeCallback(onPageChanged)
            adapter = pagerAdapter
        }
        vm.dateChanges().observe(viewLifecycleOwner) {
            WakaLog.d("New date list: ${it.dates}")
            val setPageToLast = pagerAdapter.summaryDates.isEmpty()
            pagerAdapter.summaryDates = it.dates
            if (setPageToLast) {
                viewpager_summary_container.currentItem = pagerAdapter.summaryDates.lastIndex
            }
        }
        vm.titleChanges().observe(viewLifecycleOwner) {
            textview_summary_current_date.text = it
        }
    }

    override fun onDestroyView() {
        viewpager_summary_container.unregisterOnPageChangeCallback(onPageChanged)
        super.onDestroyView()
    }

    override fun subscribeToRefreshEvents(refreshEvents: Observable<Unit>): Disposable {
        return refreshEvents.subscribe {
            eventTracker.log(Event.ManualUpdate)
            // TODO Update selected Fragment using ViewModel
        }
    }

    private class SummaryContainerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

        var summaryDates: List<DateRange> by diffUtil<DateRange, SummaryContainerAdapter>()

        override fun getItemId(position: Int): Long =
              summaryDates[position].hashCode().toLong()

        override fun containsItem(itemId: Long): Boolean =
              summaryDates.find { it.hashCode().toLong() == itemId } != null

        override fun getItemCount(): Int = summaryDates.size

        override fun createFragment(position: Int): Fragment = FragmentSummary.create(summaryDates[position])

    }

}
