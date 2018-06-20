package com.kondenko.pocketwaka.screens.stats


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kondenko.pocketwaka.App
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.domain.stats.model.StatsModel
import com.kondenko.pocketwaka.screens.base.stateful.StatefulFragment
import com.kondenko.pocketwaka.utils.report
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import javax.inject.Inject


class FragmentStatsTab : StatefulFragment<StatsModel>(ModelFragmentStats()), StatsView {

    companion object {
        const val ARG_RANGE = "range"
    }

    private var range: String? = null

    @Inject
    lateinit var presenter: StatsPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.instance.statsComponent().inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        containerId = R.id.stats_framelayout_container
        return inflater.inflate(R.layout.fragment_stats, container, false)
    }

    override fun showError(throwable: Throwable?, messageStringRes: Int?) {
        super.showError(throwable, messageStringRes)
        throwable?.report()
    }

    fun isScrollviewOnTop() = (modelFragment as ModelFragmentStats).isScrollviewOnTop()

    fun scrollDirection() = (modelFragment as ModelFragmentStats).scrollDirection

    fun subscribeToRefreshEvents(refreshEvents: Observable<Any>): Disposable {
        return refreshEvents.subscribe {
            updateData()
        }
    }

    override fun onStart() {
        super.onStart()
        presenter.attach(this)
        range = arguments?.getString(ARG_RANGE)
        updateData()
        retryClicks().subscribe { updateData() }
    }

    override fun onStop() {
        super.onStop()
        presenter.detach()
    }

    private fun updateData() {
        range?.let {
            presenter.getStats(it)
        }
    }

    override fun onDestroy() {
        App.instance.clearStatsComponent()
        super.onDestroy()
    }

}
