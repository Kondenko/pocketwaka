package com.kondenko.pocketwaka.screens.fragments.stats


import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kondenko.pocketwaka.Const
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.api.model.stats.DataWrapper
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

    private val TAG = this.javaClass.simpleName + "@" + this.hashCode()

    private lateinit var presenter: FragmentStatsPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val token = AccessTokenUtils.getTokenHeaderValue(activity)
        presenter = FragmentStatsPresenter(arguments.getString(Const.STATS_RANGE_KEY), token, this)
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
        super.onStop()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRefreshEvent(event: RefreshEvent) {
        onRefresh()
    }

    override fun onRefresh() {
        setLoadingFragment()
    }

    override fun onSuccess(dataWrapper: DataWrapper) {
        EventBus.getDefault().post(SuccessEvent)
        setContentFragment(dataWrapper)
    }

    override fun onError(error: Throwable?) {
        EventBus.getDefault().post(ErrorEvent)
        error?.let { Log.e(TAG, "FragmentStats@onError: ${error.stackTrace}") }
        setErrorFragment()
    }

    private fun setLoadingFragment() {
        val loadingFragment = FragmentLoadingState()
        setFragment(loadingFragment)
    }

    private fun setContentFragment(data: DataWrapper) {
        val fragment = if (data.stats != null && data.stats.totalSeconds > 0) {
            FragmentStatsData.newInstance(data)
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
        val transaction = childFragmentManager.beginTransaction()
        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
        transaction.replace(R.id.container_stats, fragment)
        transaction.commit()
    }

}
