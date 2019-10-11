package com.kondenko.pocketwaka.screens.menu

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.toPublisher
import com.kondenko.pocketwaka.domain.main.ClearCache
import com.kondenko.pocketwaka.domain.menu.GetMenuUiModel
import com.kondenko.pocketwaka.domain.menu.MenuUiModel
import com.kondenko.pocketwaka.screens.State
import com.kondenko.pocketwaka.screens.base.BaseViewModel
import com.kondenko.pocketwaka.utils.extensions.attachToLifecycle
import com.kondenko.pocketwaka.utils.extensions.report
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.PublishSubject

class MenuViewModel(
        private val lifecycleOwner: LifecycleOwner,
        private val clearCache: ClearCache,
        getMenuUiModel: GetMenuUiModel
) : BaseViewModel<MenuUiModel?>() {

    private val sendFeedbackClicks = PublishSubject.create<Unit>()
    private val githubClicks = PublishSubject.create<Unit>()

    private val stateObservable = Observable.fromPublisher(_state.toPublisher(lifecycleOwner))

    init {
        getMenuUiModel(onSuccess = { model: MenuUiModel ->
            setState(State.Success(model))
        })
        githubClicks.waitForData("Error opening Github") {
            setState(MenuState.OpenGithub(it))
        }
        sendFeedbackClicks.waitForData("Error fetching feedback email") {
            setState(MenuState.SendFeedback(it))
        }
    }

    private fun Observable<Unit>.waitForData(errorMessage: String, onNext: (MenuUiModel) -> Unit) {
        Observables.zip(this, stateObservable) { _, state -> state }
                .filter { it.data != null }
                .subscribeBy(
                        onNext = { onNext(it.data!!) },
                        onError = { it.report(errorMessage) }
                )
                .attachToLifecycle(lifecycleOwner)
    }

    fun onResume() {
        _state.value.let {
            if (it is MenuState.OpenGithub) {
                setState(State.Success(it.data))
            }
        }
    }

    fun sendFeedback() {
        sendFeedbackClicks.onNext(Unit)
    }

    fun openGithub() {
        githubClicks.onNext(Unit)
    }

    fun logout() {
        clearCache(onFinish = { setState(MenuState.LogOut) })
    }

}