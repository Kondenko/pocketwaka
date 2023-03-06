package com.kondenko.pocketwaka.screens.menu

import com.kondenko.pocketwaka.domain.main.ClearCache
import com.kondenko.pocketwaka.domain.menu.GetMenuUiModel
import com.kondenko.pocketwaka.domain.menu.MenuUiModel
import com.kondenko.pocketwaka.screens.State
import com.kondenko.pocketwaka.screens.base.BaseViewModel
import com.kondenko.pocketwaka.utils.extensions.report
import com.kondenko.pocketwaka.utils.extensions.toObservable
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.PublishSubject

class MenuViewModel(
      private val clearCache: ClearCache,
      getMenuUiModel: GetMenuUiModel,
) : BaseViewModel<MenuUiModel?>() {

    private val rateAppClicks = PublishSubject.create<Unit>()

    private val sendFeedbackClicks = PublishSubject.create<Unit>()

    private val githubClicks = PublishSubject.create<Unit>()

    private val privacyPolicyClicks = PublishSubject.create<Unit>()

    private val stateObservable = stateLiveData.toObservable()

    init {
        getMenuUiModel(onSuccess = { model: MenuUiModel ->
            stateLiveData.value = (State.Success(model))
        })
        githubClicks.waitForData("Error opening Github") {
            stateLiveData.value = (MenuState.OpenGithub(it))
            stateLiveData.value = (State.Success(state?.data ?: it))
        }
        privacyPolicyClicks.waitForData("Error opening Privacy Policy") {
            stateLiveData.value = (MenuState.OpenPrivacyPolicy(it))
            stateLiveData.value = (State.Success(state?.data ?: it))
        }
        sendFeedbackClicks.waitForData("Error fetching feedback email") {
            stateLiveData.value = (MenuState.SendFeedback(it))
            stateLiveData.value = (State.Success(state?.data ?: it))
        }
        rateAppClicks.waitForData("Error setting up rating dialog") {
            stateLiveData.value = (MenuState.ShowRatingDialog(it, askForFeedback = false, openPlayStore = false))
        }
    }

    private fun Observable<Unit>.waitForData(errorMessage: String, onNext: (MenuUiModel) -> Unit) {
        Observables.zip(this, stateObservable) { _, state -> state }
              .filter { it.data != null }
              .subscribeBy(
                    onNext = { onNext(it.data!!) },
                    onError = { it.report(errorMessage) }
              )
    }

    fun onResume() {
        state.let {
            if (!(it is State.Success || it is MenuState.ShowRatingDialog)) {
                setState(State.Success(it?.data))
            }
        }
    }

    fun rate(rating: Int) {
        if (rating == 0) return
        val positiveRatingThreshold = state?.data?.positiveRatingThreshold
        if (positiveRatingThreshold != null && rating < positiveRatingThreshold) {
            setState(MenuState.ShowRatingDialog(state?.data, askForFeedback = true, openPlayStore = false))
        } else {
            setState(MenuState.ShowRatingDialog(state?.data, askForFeedback = false, openPlayStore = true))
        }
    }

    fun rateApp() {
        rateAppClicks.onNext(Unit)
    }

    fun sendFeedback() {
        sendFeedbackClicks.onNext(Unit)
    }

    fun openGithub() {
        githubClicks.onNext(Unit)
    }

    fun openPrivacyPolicy() {
        privacyPolicyClicks.onNext(Unit)
    }

    fun logout() {
        clearCache(onFinish = { setState(MenuState.LogOut) })
    }

    fun onDialogDismissed() {
        if (state is MenuState.ShowRatingDialog) {
            setState(State.Success(state?.data))
        }
    }

}