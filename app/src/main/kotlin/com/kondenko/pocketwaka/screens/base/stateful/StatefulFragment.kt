package com.kondenko.pocketwaka.screens.base.stateful

import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.Fragment
import com.kondenko.pocketwaka.screens.base.stateful.states.FragmentEmptyState
import com.kondenko.pocketwaka.screens.base.stateful.states.FragmentErrorState
import com.kondenko.pocketwaka.screens.base.stateful.states.FragmentLoadingState
import com.kondenko.pocketwaka.utils.transaction
import io.reactivex.subjects.BehaviorSubject


abstract class StatefulFragment<M : Parcelable>(private val modelFragment: ModelFragment<M>) : Fragment(), StatefulView<M> {

    private val ARG_MODEL = "ARG_MODEL"

    protected var containerId: Int = 0

    private val errorFragment = FragmentErrorState()

    private val emptyFragment by lazy {
        FragmentEmptyState()
    }

    private val loadingFragment by lazy {
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
        errorFragment.show()
    }

    override fun showEmptyState() {
        emptyFragment.show()
    }

    override fun showLoading() {
        loadingFragment.show()
    }

    protected fun retryClicks() = errorFragment.retryClicks()

    private fun Fragment.show() {
        this@StatefulFragment.childFragmentManager.transaction {
            setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            replace(containerId, this@show)
        }
    }

}