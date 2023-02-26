package com.kondenko.pocketwaka.screens.base

import android.content.Context
import android.net.Uri
import androidx.annotation.CallSuper
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.kondenko.pocketwaka.Const
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.analytics.Event
import com.kondenko.pocketwaka.analytics.EventTracker
import com.kondenko.pocketwaka.screens.ScreenStatus
import com.kondenko.pocketwaka.screens.State
import com.kondenko.pocketwaka.screens.StateFragment
import com.kondenko.pocketwaka.screens.main.MainViewModel
import com.kondenko.pocketwaka.screens.main.OnLogOut
import com.kondenko.pocketwaka.ui.skeleton.RecyclerViewSkeleton
import com.kondenko.pocketwaka.ui.skeleton.SkeletonAdapter
import com.kondenko.pocketwaka.utils.extensions.report
import com.kondenko.pocketwaka.utils.extensions.transaction
import org.koin.android.ext.android.inject
import org.koin.androidx.scope.ScopeFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

abstract class BaseFragment<T, ST, A : SkeletonAdapter<T, *>, in S : State<ST>> : ScopeFragment() {

    protected open val stateFragment = StateFragment()

    protected abstract val containerId: Int

    protected lateinit var listSkeleton: RecyclerViewSkeleton<T, A>

    private val fragmentStateTag = "state"

    private val eventTracker: EventTracker by inject()

    private val onLogOut: OnLogOut by sharedViewModel<MainViewModel>()


    protected abstract fun updateData(data: ST?, status: ScreenStatus? = null)

    protected abstract fun reloadScreen()

    protected abstract fun getDataView(): RecyclerView

    @CallSuper
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (childFragmentManager.findFragmentByTag(fragmentStateTag) == null) {
            childFragmentManager.transaction {
                add(containerId, stateFragment, fragmentStateTag)
            }
        }
    }

    protected fun State<ST>.render() {
        listSkeleton.show(getDataView(), (this as? State.Loading<*>)?.isInterrupting == true)
        when (this) {
            is State.Success -> render()
            is State.Loading -> render()
            is State.Offline -> render()
            is State.Empty -> render()
            is State.Failure -> render()
        }
    }

    private fun State.Loading<ST>.render() {
        showData(true)
        if (!isInterrupting) {
            updateData(data, ScreenStatus.Loading())
        }
    }

    private fun State.Success<ST>.render() {
        showData(true)
        updateData(data)
    }

    private fun State.Failure<ST>.render() {
        if (this !is State.Failure.Unauthorized) {
            exception?.report()
            showData(!isFatal)
            if (isFatal) {
                stateFragment.setState(this, this@BaseFragment::reloadScreen)
            } else {
                updateData(data)
                view?.let {
                    val errorRes = when (this) {
                        is State.Failure.Unknown -> R.string.stats_error_unknown
                        is State.Failure.InvalidParams -> R.string.stats_error_unknown_range
                        is State.Failure.NoNetwork -> R.string.stats_error_unknown_range_no_network
                        is State.Failure.Unauthorized -> R.string.all_error_invalid_access
                    }
                    Snackbar.make(it, errorRes, Snackbar.LENGTH_SHORT).show()
                }
            }
        } else {
            forceLogOut()
        }
    }

    private fun State.Offline<ST>.render() {
        showData(data != null)
        if (data == null) {
            stateFragment.setState(this)
        } else {
            updateData(data, ScreenStatus.Offline())
        }
    }

    private fun State.Empty.render() {
        showData(false)
        stateFragment.setState(this) { openPlugins() }
    }

    protected fun showData(show: Boolean) {
        getDataView().isVisible = show
        stateFragment.let {
            childFragmentManager.transaction {
                if (show) hide(it)
                else show(it)
            }
        }
    }

    private fun openPlugins() = context?.let {
        val uri = Const.URL_PLUGINS
        val builder = CustomTabsIntent.Builder()
        builder.setToolbarColor(ContextCompat.getColor(it, R.color.color_primary_light))
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(it, Uri.parse(uri))
    }

    private fun forceLogOut() {
        eventTracker.log(Event.ForcedLogout)
        onLogOut.logOut(forced = true)
    }

}