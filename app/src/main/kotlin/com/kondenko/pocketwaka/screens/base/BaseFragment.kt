package com.kondenko.pocketwaka.screens.base

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.kondenko.pocketwaka.Const
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.screens.ScreenStatus
import com.kondenko.pocketwaka.screens.State
import com.kondenko.pocketwaka.screens.StateFragment
import com.kondenko.pocketwaka.ui.skeleton.RecyclerViewSkeleton
import com.kondenko.pocketwaka.ui.skeleton.SkeletonAdapter
import com.kondenko.pocketwaka.utils.extensions.report
import com.kondenko.pocketwaka.utils.extensions.transaction

abstract class BaseFragment<T, ST, A : SkeletonAdapter<T, *>, in S : State<ST>> : Fragment() {

    protected open val stateFragment = StateFragment()
    private val fragmentStateTag = "state"

    protected abstract val containerId: Int

    protected lateinit var listSkeleton: RecyclerViewSkeleton<T, A>

    protected abstract fun updateData(data: ST?, status: ScreenStatus? = null)

    protected abstract fun reloadScreen()

    protected abstract fun provideDataView(): View

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (childFragmentManager.findFragmentByTag(fragmentStateTag) == null) {
            childFragmentManager.transaction {
                add(containerId, stateFragment, fragmentStateTag)
            }
        }
    }

    protected fun State<ST>.render() {
        listSkeleton.show((this as? State.Loading<*>)?.isInterrupting == true)
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
                }
                Snackbar.make(it, errorRes, Snackbar.LENGTH_SHORT).show()
            }
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
        stateFragment.setState(this, ::openPlugins)
    }

    protected fun showData(show: Boolean) {
        provideDataView().isVisible = show
        stateFragment.let {
            childFragmentManager.transaction {
                if (show) hide(it)
                else show(it)
            }
        }
    }

    private fun openPlugins() {
        val uri = Const.URL_PLUGINS
        val builder = CustomTabsIntent.Builder()
        builder.setToolbarColor(ContextCompat.getColor(context!!, R.color.color_primary_light))
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(context, Uri.parse(uri))
    }

}