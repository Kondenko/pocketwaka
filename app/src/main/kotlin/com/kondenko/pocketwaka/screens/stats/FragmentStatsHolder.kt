package com.kondenko.pocketwaka.screens.stats


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kondenko.pocketwaka.App
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.domain.stats.model.StatsModel
import com.kondenko.pocketwaka.events.RefreshEvent
import com.kondenko.pocketwaka.screens.base.stateful.StatefulFragment
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject


class FragmentStatsHolder : StatefulFragment<StatsModel>(), StatsView {

    companion object {
        const val ARG_RANGE = "range"
    }

    lateinit var range: String

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

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
        presenter.attach(this)
        range = arguments?.getString(ARG_RANGE)?:"unknown_range"
        if (modelFragment == null) presenter.getStats(range)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
        presenter.detach()
    }

    override fun initModelFragment(model: StatsModel) = ModelFragmentStats.create(model)

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRefresh(event: RefreshEvent) = presenter.getStats(range)

}
