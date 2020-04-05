package com.kondenko.pocketwaka.screens.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kondenko.pocketwaka.domain.UseCaseCompletable
import com.kondenko.pocketwaka.domain.UseCaseSingle
import com.kondenko.pocketwaka.domain.main.FetchRemoteConfigValues
import com.kondenko.pocketwaka.domain.main.RefreshAccessToken
import com.kondenko.pocketwaka.screens.main.MainAction.GoToContent
import com.kondenko.pocketwaka.screens.main.MainAction.GoToLogin
import com.kondenko.pocketwaka.utils.WakaLog
import com.kondenko.pocketwaka.utils.extensions.report


class MainViewModel(
      private val checkIfUserIsLoggedIn: UseCaseSingle<Nothing, Boolean>,
      private val clearCache: UseCaseCompletable<Nothing>,
      private val refreshAccessToken: RefreshAccessToken,
      fetchRemoteConfigValues: FetchRemoteConfigValues
) : ViewModel(), OnLogIn, OnLogOut {

    private val action = MutableLiveData<MainAction>()

    init {
        checkIfLoggedIn()
        fetchRemoteConfigValues(
              onSuccess = { WakaLog.d("Remote config values updated") },
              onError = { it.report() }
        )
    }

    fun actions(): LiveData<MainAction> = action

    private fun checkIfLoggedIn() {
        checkIfUserIsLoggedIn(
              onSuccess = { isLoggedIn ->
                  if (isLoggedIn) {
                      refreshAccessToken.invoke()
                      action.postValue(GoToContent)
                  } else {
                      action.postValue(GoToLogin())
                  }
              },
              onError = { error ->
                  clearCache(onFinish = { action.postValue(GoToLogin()) })
              }
        )
    }

    override fun logIn() {
        action.value = GoToContent
    }

    override fun openWebView(url: String, redirectUrl: String) {
        action.value = MainAction.OpenWebView(url, redirectUrl)
    }

    override fun closeWebView() {
        action.postValue(MainAction.CloseWebView)
    }

    override fun logOut(forced: Boolean) {
        action.value = GoToLogin(forced)
    }

    override fun onCleared() {
        checkIfUserIsLoggedIn.dispose()
        refreshAccessToken.dispose()
        super.onCleared()
    }

}