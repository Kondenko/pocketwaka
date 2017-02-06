package com.kondenko.pocketwaka.screens.fragments.stats


import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kondenko.pocketwaka.Const
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.api.model.stats.DataWrapper
import com.kondenko.pocketwaka.api.oauth.AccessTokenUtils
import com.kondenko.pocketwaka.databinding.FragmentStatsBinding
import com.kondenko.pocketwaka.events.RefreshEvent
import com.kondenko.pocketwaka.events.TabsAnimationEvent
import com.kondenko.pocketwaka.ui.CardStats
import com.kondenko.pocketwaka.ui.ObservableScrollView
import com.kondenko.pocketwaka.ui.OnScrollViewListener
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*


class FragmentStats : Fragment(), FragmentStatsView {

    private lateinit var binding: FragmentStatsBinding
    private lateinit var presenter: FragmentStatsPresenter

    private var shadowAnimationNeeded = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val token = AccessTokenUtils.getTokenHeaderValue(activity)
        presenter = FragmentStatsPresenter(arguments.getString(Const.STATS_RANGE_KEY), token, this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate<FragmentStatsBinding>(inflater, R.layout.fragment_stats, container, false)
        binding.statsScrollView.setOnScrollListener(object : OnScrollViewListener {
            override fun onScrollChanged(scrollView: ObservableScrollView, x: Int, y: Int, oldX: Int, oldY: Int) {
                if (y >= 10) {
                    if (shadowAnimationNeeded) {
                        binding.shadowView.animate().alpha(Const.MAX_SHADOW_OPACITY)
                        EventBus.getDefault().post(TabsAnimationEvent(false))
                    }
                    shadowAnimationNeeded = false
                } else {
                    binding.shadowView.animate().alpha(0f)
                    EventBus.getDefault().post(TabsAnimationEvent(true))
                    shadowAnimationNeeded = true
                }
            }

        })
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume()
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRefreshEvent(event: RefreshEvent) = presenter.getStats()

    override fun onSuccess(dataWrapper: DataWrapper) {
        setLoading(false)
        binding.dataWrapper = dataWrapper
        binding.executePendingBindings()
        addStatsCards(dataWrapper)
    }

    override fun onError(error: Throwable?, messageString: Int) {
        setLoading(false)
        error?.printStackTrace()
        Snackbar.make(binding.rootLayout, messageString, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.str_action_retry, { view ->
                    view.setOnClickListener { presenter.getStats() }
                })
                .show()
    }

    override fun setLoading(loading: Boolean) {
        binding.include.textViewCaption.visibility = if (loading) View.INVISIBLE else View.VISIBLE
    }

    private fun addStatsCards(dataWrapper: DataWrapper) {
        if (binding.linearLayoutCards.childCount <= 0) {
            val cards = getAvailableCards(dataWrapper)
            for (card in cards) {
                binding.linearLayoutCards.addView(card.getView())
            }
        }
    }

    fun getAvailableCards(dataWrapper: DataWrapper): ArrayList<CardStats> {
        val cards = ArrayList<CardStats>()
        val projects = dataWrapper.stats.projects
        val editors = dataWrapper.stats.editors
        val languages = dataWrapper.stats.languages
        val operatingSystems = dataWrapper.stats.operatingSystems
        projects?.let { cards.add(CardStats(context, CardStats.TYPE_PROJECTS, it)) }
        editors?.let { cards.add(CardStats(context, CardStats.TYPE_EDITORS, it)) }
        languages?.let { cards.add(CardStats(context, CardStats.TYPE_LANGUAGES, it)) }
        operatingSystems?.let { cards.add(CardStats(context, CardStats.TYPE_OPERATING_SYSTEMS, it)) }
        return cards
    }


}
