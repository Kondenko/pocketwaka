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
        val ARG_RANGE = "range"
    }

    @Inject
    lateinit var presenter: StatsPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.statsComponent.inject(this)
        presenter.attach(this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_stats, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val range = arguments.getString(ARG_RANGE)
        presenter.getStats(range)
    }

    override fun onSuccess(result: Stats?) {
        result?.let { modelFragment = ModelFragmentStats.create(it) }
        super.onSuccess(result)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detach()
    }

}
