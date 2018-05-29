package com.kondenko.pocketwaka.screens.stats


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kondenko.pocketwaka.App
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.data.stats.model.Stats
import com.kondenko.pocketwaka.screens.StatefulFragment
import javax.inject.Inject


class FragmentStatsHolder : StatefulFragment<Stats>(), StatsView {

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
        return inflater.inflate(R.layout.fragment_stats, container, false)
    }

    override fun onStart() {
        super.onStart()
        presenter.attach(this)
        containerId = R.id.stats_framelayout_container
        val range = arguments?.getString(ARG_RANGE)
        if (modelFragment == null && range != null) presenter.getStats(range)
    }

    override fun onStop() {
        super.onStop()
        presenter.detach()
    }

    override fun onSuccess(result: Stats?) {
        result?.let { modelFragment = ModelFragmentStats.create(it) }
        super.onSuccess(result)
    }

}
