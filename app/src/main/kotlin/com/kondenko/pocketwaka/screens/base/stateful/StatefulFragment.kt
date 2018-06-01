package com.kondenko.pocketwaka.screens.base.stateful

import android.os.Parcelable
import android.support.v4.app.Fragment
import com.kondenko.pocketwaka.screens.base.stateful.states.FragmentEmptyState
import com.kondenko.pocketwaka.screens.base.stateful.states.FragmentErrorState
import com.kondenko.pocketwaka.screens.base.stateful.states.FragmentLoadingState
import com.kondenko.pocketwaka.utils.transaction
import io.reactivex.Observable
import timber.log.Timber


abstract class StatefulFragment<M : Parcelable> : Fragment(), StatefulView<M> {

    protected var containerId: Int = 0

    private val emptyFragment by lazy {
        FragmentEmptyState()
    }

    private val errorFragment by lazy {
        val fragment = FragmentErrorState()
        retryClicks.switchMap { fragment.retryClicks() }
        fragment // return value
    }

    private val loadingFragment by lazy {
        FragmentLoadingState()
    }

    protected var modelFragment: ModelFragment<M>? = null

    protected val retryClicks: Observable<Any> = Observable.never()

    abstract fun initModelFragment(model: M): ModelFragment<M>

    override fun showModel(model: M) {
        modelFragment = initModelFragment(model).apply {
            this.show(ModelFragment.TAG)
        }
    }

    override fun showError(throwable: Throwable?, messageStringRes: Int?) {
        Timber.e(throwable)
        messageStringRes?.let { errorFragment.setMessage(activity!!.getString(messageStringRes)) }
        errorFragment.show(errorFragment.TAG)
    }

    override fun showEmptyState() {
        emptyFragment.show(emptyFragment.TAG)
    }

    override fun showLoading() {
        loadingFragment.show(loadingFragment.TAG)
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