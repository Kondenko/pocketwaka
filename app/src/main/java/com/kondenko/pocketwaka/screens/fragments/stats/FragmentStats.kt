package com.kondenko.pocketwaka.screens.fragments.stats


import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.api.model.stats.Stats
import com.kondenko.pocketwaka.api.model.stats.DataWrapper
import com.kondenko.pocketwaka.api.oauth.AccessTokenUtils
import com.kondenko.pocketwaka.databinding.FragmentStatsBinding


class FragmentStats : Fragment(), FragmentStatsView {

    private lateinit var binding: FragmentStatsBinding
    private lateinit var presenter: FragmentStatsPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val token = AccessTokenUtils.getTokenHeaderValue(activity)
        presenter = FragmentStatsPresenter(token, this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate<FragmentStatsBinding>(inflater, R.layout.fragment_stats, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume()
    }

    override fun onSuccess(dataWrapper: DataWrapper) {
        binding.dataWrapper = dataWrapper
        binding.executePendingBindings()
    }

    override fun onError(error: Throwable?, messageString: Int) {
        error?.printStackTrace()
        Toast.makeText(activity, activity.getString(messageString), Toast.LENGTH_SHORT).show()
    }

}
