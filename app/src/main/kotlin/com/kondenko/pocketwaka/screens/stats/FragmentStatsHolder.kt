package com.kondenko.pocketwaka.screens.stats


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kondenko.pocketwaka.App
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.domain.stats.model.StatsModel
import com.kondenko.pocketwaka.screens.base.stateful.StatefulFragment
import javax.inject.Inject


class FragmentStatsHolder : StatefulFragment<StatsModel>(), StatsView {

    companion object {
        const val ARG_RANGE = "range"
    }

    @Inject
    lateinit var presenter: StatsPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.statsComponent.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        containerId = R.id.stats_framelayout_container
        return inflater.inflate(R.layout.fragment_stats, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.attach(this)
        val range = arguments?.getString(ARG_RANGE)
        if (range != null && modelFragment == null) presenter.getStats(range)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.detach()
    }

    override fun onSuccess(result: StatsModel?) {
        result?.let {
            modelFragment = ModelFragmentStats.create(result)
        }
        super.onSuccess(result)
    }
}
