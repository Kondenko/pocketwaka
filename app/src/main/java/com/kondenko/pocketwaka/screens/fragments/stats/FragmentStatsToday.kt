package com.kondenko.pocketwaka.screens.fragments.stats


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.kondenko.pocketwaka.R

/**
 * A simple [Fragment] subclass.
 */
class FragmentStatsToday : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_stats_today, container, false)
    }

}
