package com.kondenko.pocketwaka.screens.stats


import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kondenko.pocketwaka.Const
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.data.auth.repository.AccessTokenUtils
import com.kondenko.pocketwaka.data.stats.model.StatsDataWrapper
import com.kondenko.pocketwaka.events.ErrorEvent
import com.kondenko.pocketwaka.events.RefreshEvent
import com.kondenko.pocketwaka.events.SuccessEvent
import com.kondenko.pocketwaka.screens.states.FragmentEmptyState
import com.kondenko.pocketwaka.screens.states.FragmentErrorState
import com.kondenko.pocketwaka.screens.states.FragmentLoadingState
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject


class FragmentStats : Fragment(), StatsView {

    private lateinit var TAG: String

    @Inject
    lateinit var presenter: StatsPresenter

    private val fragmentEmptyState by lazy {
        FragmentEmptyState()
    }

    private val fragmentErrorState by lazy {
        FragmentErrorState()
    }

    private val fragmentLoadingState by lazy {
        FragmentLoadingState()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TAG = this.javaClass.simpleName + "@" + arguments.getString(Const.STATS_RANGE_KEY)
        val token = AccessTokenUtils.getTokenHeaderValue(activity)
//        presenter = StatsPresenter(arguments.getString(Const.STATS_RANGE_KEY), token, this)
        fragmentErrorState.setOnUpdateListener { EventBus.getDefault().post(RefreshEvent) }
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showLoadingState()
        presenter.onViewCreated(context)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_stats, container, false)
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        presenter.onStop()
        super.onStop()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRefreshEvent(event: RefreshEvent) {
        onRefresh()
    }

    override fun onRefresh() {
        showLoadingState()
//        presenter.updateData(context,, )
    }

    override fun onSuccess(statsDataWrapper: StatsDataWrapper) {
        EventBus.getDefault().post(SuccessEvent)
        showContent(statsDataWrapper)
    }

    override fun onError(error: Throwable?) {
        Log.i(TAG, "onError")
        EventBus.getDefault().post(ErrorEvent)
        error?.printStackTrace()
        showError()
    }

    private fun showContent(statsData: StatsDataWrapper) {
        if (statsData.stats != null) {
            setFragment(FragmentStatsData.newInstance(statsData))
        } else {
            setFragment(fragmentEmptyState)
        }
    }

    private fun showLoadingState() {
        setFragment(fragmentLoadingState)
    }

    private fun showError() {
        setFragment(fragmentErrorState)
    }

    private fun setFragment(fragment: Fragment) {
        if (activity != null && !activity.isDestroyed) {
            Log.i(TAG, "setFragment: $fragment")
            val transaction = childFragmentManager.beginTransaction()
            transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            transaction.replace(R.id.container_stats, fragment)
            transaction.commit()
        }
    }

}
