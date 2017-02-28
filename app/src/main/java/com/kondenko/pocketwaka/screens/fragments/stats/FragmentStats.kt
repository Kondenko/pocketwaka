package com.kondenko.pocketwaka.screens.fragments.stats


import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kondenko.pocketwaka.Const
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.api.model.stats.StatsDataWrapper
import com.kondenko.pocketwaka.api.oauth.AccessTokenUtils
import com.kondenko.pocketwaka.events.ErrorEvent
import com.kondenko.pocketwaka.events.RefreshEvent
import com.kondenko.pocketwaka.events.SuccessEvent
import com.kondenko.pocketwaka.screens.fragments.states.FragmentEmptyState
import com.kondenko.pocketwaka.screens.fragments.states.FragmentErrorState
import com.kondenko.pocketwaka.screens.fragments.states.FragmentLoadingState
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class FragmentStats : Fragment(), FragmentStatsView {

    private lateinit var TAG: String

    private lateinit var presenter: FragmentStatsPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TAG = this.javaClass.simpleName + "@" + arguments.getString(Const.STATS_RANGE_KEY)
        val token = AccessTokenUtils.getTokenHeaderValue(activity)
        presenter = FragmentStatsPresenter(arguments.getString(Const.STATS_RANGE_KEY), token, this)
        presenter.onCreate()
        setLoadingFragment()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_stats, container, false)
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
        presenter.onStart()
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
        setLoadingFragment()
        presenter.updateData()
    }

    override fun onSuccess(statsDataWrapper: StatsDataWrapper) {
        EventBus.getDefault().post(SuccessEvent)
        setContentFragment(statsDataWrapper)
    }

    override fun onError(error: Throwable?) {
        Log.i(TAG, "onError")
        EventBus.getDefault().post(ErrorEvent)
        error?.printStackTrace()
        setErrorFragment()
    }

    private fun setLoadingFragment() {
        val loadingFragment = FragmentLoadingState()
        setFragment(loadingFragment)
    }

    private fun setContentFragment(statsData: StatsDataWrapper) {
        val fragment = if (statsData.stats != null && statsData.stats.totalSeconds > 0) {
            FragmentStatsData.newInstance(statsData)
        } else {
            FragmentEmptyState()
        }
        setFragment(fragment)
    }

    private fun setErrorFragment() {
        val errorFragment = FragmentErrorState()
        errorFragment.setOnUpdateListener {
            EventBus.getDefault().post(RefreshEvent)
        }
        setFragment(errorFragment)
    }

    private fun setFragment(fragment: Fragment) {
        if (activity != null && !activity.isDestroyed) {
            val transaction = childFragmentManager.beginTransaction()
            transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            transaction.replace(R.id.container_stats, fragment)
            transaction.commit()
        }
    }

}
