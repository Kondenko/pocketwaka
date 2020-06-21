package com.kondenko.pocketwaka.screens.summary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.Tags.DATE_PICKER
import com.kondenko.pocketwaka.analytics.Event
import com.kondenko.pocketwaka.analytics.EventTracker
import com.kondenko.pocketwaka.screens.Refreshable
import com.kondenko.pocketwaka.utils.WakaLog
import com.kondenko.pocketwaka.utils.date.DateRange
import com.kondenko.pocketwaka.utils.diffutil.diffUtil
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_summary_container.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class FragmentSummaryContainer : Fragment(), Refreshable {

    private val rangeViewModel: SummaryRangeViewModel by sharedViewModel()

    private val eventTracker: EventTracker by inject()

    private lateinit var pagerAdapter: SummaryContainerAdapter

    private val onPageChanged = object : ViewPager2.OnPageChangeCallback() {

        private var position = -1;

        override fun onPageSelected(position: Int) {
            this.position = position
        }

        override fun onPageScrollStateChanged(state: Int) {
            super.onPageScrollStateChanged(state)
            if (state == ViewPager2.SCROLL_STATE_IDLE) {
                val day = pagerAdapter.summaryDates[position] as? DateRange.SingleDay
                WakaLog.d(DATE_PICKER, "onPageSelected($position), day = $day")
                day?.let { rangeViewModel.selectDate(it, false) }
            }
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
        rangeViewModel.dateChanges().observe(viewLifecycleOwner) {
            WakaLog.d(DATE_PICKER, "New date list: ${it.dates}")
            pagerAdapter.summaryDates = it.dates
            if (it.invalidateScreens) {
                viewpager_summary_container.invalidate()
                viewpager_summary_container.currentItem = pagerAdapter.summaryDates.lastIndex.also {
                    WakaLog.d(DATE_PICKER, "Setting current item to $it")
                }
            }
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

        var summaryDates: List<DateRange> by diffUtil()

        override fun getItemId(position: Int): Long =
              summaryDates[position].hashCode().toLong()

        override fun containsItem(itemId: Long): Boolean =
              summaryDates.find { it.hashCode().toLong() == itemId } != null

        override fun getItemCount(): Int = summaryDates.size

        override fun createFragment(position: Int): Fragment = FragmentSummary.create(summaryDates[position])

    }

}
