package com.kondenko.pocketwaka.screens.base.stateful

import android.os.Parcelable
import android.support.v4.app.Fragment
import com.kondenko.pocketwaka.screens.base.stateful.states.FragmentEmptyState
import com.kondenko.pocketwaka.screens.base.stateful.states.FragmentErrorState
import com.kondenko.pocketwaka.screens.base.stateful.states.FragmentLoadingState
import com.kondenko.pocketwaka.ui.LogFragment
import com.kondenko.pocketwaka.utils.transaction
import timber.log.Timber


abstract class StatefulFragment<M : Parcelable> : LogFragment(), StatefulView<M> {

    protected var containerId: Int = 0

    private val emptyFragment by lazy {
        FragmentEmptyState()
    }

    private val errorFragment by lazy {
        FragmentErrorState()
    }

    private val loadingFragment by lazy {
        FragmentLoadingState()
    }

    protected var modelFragment: ModelFragment<M>? = null

    override fun onSuccess(result: M?) {
        if (result != null) {
            if (modelFragment != null) modelFragment!!.show(ModelFragment.TAG)
            else errorFragment.show(errorFragment.TAG)
        } else {
            emptyFragment.show(emptyFragment.TAG)
        }
    }

    override fun onError(throwable: Throwable?, messageStringRes: Int?) {
        Timber.e(throwable)
        messageStringRes?.let { errorFragment.setMessage(activity!!.getString(messageStringRes)) }
        errorFragment.show(errorFragment.TAG)
    }

    override fun onRefresh() {
        setLoading(true)
    }

    override fun setLoading(isLoading: Boolean) {
        if (isLoading) loadingFragment.show(loadingFragment.TAG)
    }

    private fun Fragment.show(tag: String) {
        if (this@StatefulFragment.childFragmentManager.findFragmentByTag(tag) == null) {
            this@StatefulFragment.childFragmentManager.transaction {
                setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                replace(containerId, this@show, tag)
            }
        }
    }


}