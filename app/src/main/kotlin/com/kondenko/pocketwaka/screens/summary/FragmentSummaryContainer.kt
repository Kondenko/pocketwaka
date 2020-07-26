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
import com.kondenko.pocketwaka.analytics.Event
import com.kondenko.pocketwaka.analytics.EventTracker
import com.kondenko.pocketwaka.screens.Refreshable
import com.kondenko.pocketwaka.utils.date.DateRange
import com.kondenko.pocketwaka.utils.diffutil.diffUtil
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_summary_container.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class FragmentSummaryContainer : Fragment(), Refreshable {

    private val rangeViewModel: DatePickerViewModel by sharedViewModel()

    private val eventTracker: EventTracker by inject()

    private lateinit var pagerAdapter: SummaryContainerAdapter

    private val onPageChanged = object : ViewPager2.OnPageChangeCallback() {

        private var position = -1;

        override fun onPageSelected(position: Int) {
            this.position = position
        }

        override fun onPageScrollStateChanged(state: Int) {
            super.onPageScrollStateChanged(state)
            if (state == ViewPager2.SCROLL_STATE_IDLE && position in pagerAdapter.dates.indices) {
                val day = pagerAdapter.dates[position] as? DateRange.SingleDay
                day?.let { rangeViewModel.selectDate(it, false) }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
          inflater.inflate(R.layout.fragment_summary_container, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pagerAdapter = SummaryContainerAdapter(this)
        with(viewpager_summary_container) {
            registerOnPageChangeCallback(onPageChanged)
            adapter = pagerAdapter
        }
        rangeViewModel.dateChanges().observe(viewLifecycleOwner) { state ->
            pagerAdapter.dates = state.dates
            if (state.invalidateScreens) {
                viewpager_summary_container.post {
                    viewpager_summary_container.currentItem = (state.dates.lastIndex)
                          .let { if (state.openLastItem) it else it / 2 }
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
            // (secondary) TODO Update selected Fragment using ViewModel
        }
    }

    private class SummaryContainerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

        var dates: List<DateRange> by diffUtil()

        private val DateRange.id
            get() = hashCode().toLong()

        override fun createFragment(position: Int): Fragment = FragmentSummary.create(dates[position])

        override fun getItemId(position: Int): Long = dates[position].id

        override fun containsItem(itemId: Long): Boolean = dates.indexOfFirst { it.id == itemId } >= 0

        override fun getItemCount(): Int = dates.size

    }

}
