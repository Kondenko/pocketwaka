package com.kondenko.pocketwaka.screens.daily

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kondenko.pocketwaka.R
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentDayStats : Fragment() {

    private val vm: DayStatsViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_day_stats, container, false)
    }

}
