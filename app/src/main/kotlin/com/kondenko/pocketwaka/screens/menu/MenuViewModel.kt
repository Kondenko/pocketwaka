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

class MenuViewModel(lifecycleOwner: LifecycleOwner, private val clearCache: ClearCache, getMenuUiModel: GetMenuUiModel) : BaseViewModel<MenuUiModel?>() {

    private val githubClicks = PublishSubject.create<Unit>()

    private val stateObservable = Observable.fromPublisher(_state.toPublisher(lifecycleOwner))

    init {
        getMenuUiModel(onSuccess = { model: MenuUiModel ->
            setState(State.Success(model))
        })
        Observables.zip(githubClicks, stateObservable) { _, state -> state }
                .filter { (it as? State.Success)?.data?.githubUrl != null }
                .subscribeBy(
                        onNext = { setState(MenuState.OpenGithub(it.data)) },
                        onError = { it.report("Error opening Github") }
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

    fun openGithub() {
        githubClicks.onNext(Unit)
    }

    fun logout() {
        clearCache(onFinish = { setState(MenuState.LogOut) })
    }

}