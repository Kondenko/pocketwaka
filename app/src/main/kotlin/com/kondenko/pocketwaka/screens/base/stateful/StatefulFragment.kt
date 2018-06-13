package com.kondenko.pocketwaka.screens.base.stateful

import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.Fragment
import com.kondenko.pocketwaka.screens.base.stateful.states.FragmentEmptyState
import com.kondenko.pocketwaka.screens.base.stateful.states.FragmentErrorState
import com.kondenko.pocketwaka.screens.base.stateful.states.FragmentLoadingState
import com.kondenko.pocketwaka.utils.transaction
import io.reactivex.subjects.BehaviorSubject


abstract class StatefulFragment<M : Parcelable>(protected val modelFragment: ModelFragment<M>) : Fragment(), StatefulView<M> {

    private val ARG_MODEL = "ARG_MODEL"

    protected var containerId: Int = 0

    protected val errorFragment = FragmentErrorState()

    protected val emptyFragment by lazy {
        FragmentEmptyState()
    }

    protected val loadingFragment by lazy {
        FragmentLoadingState()
    }

    private val modelSubject = BehaviorSubject.create<M>()

    init {
        modelSubject
                .distinctUntilChanged()
                .doOnNext {
                    arguments?.putParcelable(ARG_MODEL, it)
                    modelFragment.show()
                }
                .doOnError {
                    showError(it)
                }
                .subscribe()
        modelFragment.subscribeToModelChanges(modelSubject.share())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedInstanceState?.getParcelable<M>(ARG_MODEL)?.let(modelSubject::onNext)
    }

    override fun showModel(model: M) {
        modelSubject.onNext(model)
    }

    override fun showError(throwable: Throwable?, messageStringRes: Int?) {
        messageStringRes?.let { errorFragment.setMessage(activity!!.getString(messageStringRes)) }
        errorFragment.show(errorFragment.TAG)
    }

    override fun showEmptyState() {
        emptyFragment.show(emptyFragment.TAG)
    }

    override fun showLoading() {
        loadingFragment.show(loadingFragment.TAG)
    }

    protected fun retryClicks() = errorFragment.retryClicks()

    /**
     * Replace the fragment with the new fragment if they differ.
     *
     * @param tag the unique string for each fragment type. Its nullablility is a workaround because the ModelFragment's view is null after a manual update.
     */
    private fun Fragment.show(tag: String? = null) {
        if (this@StatefulFragment.childFragmentManager.findFragmentByTag(tag) == null || tag == null) {
            this@StatefulFragment.childFragmentManager.transaction {
                setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                replace(containerId, this@show, tag)
            }
        }
    }

}