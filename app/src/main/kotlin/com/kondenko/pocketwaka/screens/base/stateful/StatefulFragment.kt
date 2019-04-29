package com.kondenko.pocketwaka.screens.base.stateful

import android.os.Bundle
import android.os.Parcelable
import com.kondenko.pocketwaka.screens.base.stateful.states.FragmentEmptyState
import com.kondenko.pocketwaka.screens.base.stateful.states.FragmentErrorState
import com.kondenko.pocketwaka.screens.base.stateful.states.FragmentLoadingState
import com.kondenko.pocketwaka.utils.transaction
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber


private const val argModel = "model"

abstract class StatefulFragment<M : Parcelable>(protected val modelFragment: ModelFragment<M>) : androidx.fragment.app.Fragment(), StatefulView<M> {

    protected var containerId: Int = 0

    private val modelSubject = BehaviorSubject.create<M>()

    private val errorFragment = FragmentErrorState()

    private val emptyFragment by lazy {
        FragmentEmptyState()
    }

    private val loadingFragment by lazy {
        FragmentLoadingState()
    }

    init {
        modelSubject
                .distinctUntilChanged()
                .subscribeBy(
                        onNext = {
                            arguments?.putParcelable(argModel, it)
                            modelFragment.show()
                        },
                        onError = {
                            showError(it)
                        }
                )
        modelFragment.subscribeToModelChanges(modelSubject.share())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedInstanceState?.getParcelable<M>(argModel)?.let(modelSubject::onNext)
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
    private fun androidx.fragment.app.Fragment.show(tag: String? = null) {
        if (this@StatefulFragment.childFragmentManager.findFragmentByTag(tag) == null || tag == null) {
            Timber.i("Showing fragment with tag $tag")
            this@StatefulFragment.childFragmentManager.transaction {
                setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                replace(containerId, this@show, tag)
            }
        }
    }

}