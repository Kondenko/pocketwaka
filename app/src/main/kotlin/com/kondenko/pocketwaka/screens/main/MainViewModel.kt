package com.kondenko.pocketwaka.screens.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kondenko.pocketwaka.domain.UseCaseCompletable
import com.kondenko.pocketwaka.domain.UseCaseSingle
import com.kondenko.pocketwaka.domain.main.FetchRemoteConfigValues
import com.kondenko.pocketwaka.domain.main.RefreshAccessToken
import com.kondenko.pocketwaka.screens.main.MainState.GoToLogin
import com.kondenko.pocketwaka.screens.main.MainState.GoToContent
import com.kondenko.pocketwaka.utils.WakaLog
import com.kondenko.pocketwaka.utils.extensions.report


class MainViewModel(
      private val checkIfUserIsLoggedIn: UseCaseSingle<Nothing, Boolean>,
      private val clearCache: UseCaseCompletable<Nothing>,
      private val refreshAccessToken: RefreshAccessToken,
      fetchRemoteConfigValues: FetchRemoteConfigValues
) : ViewModel(), OnLogIn, OnLogOut {

    private val state = MutableLiveData<MainState>()

    init {
        checkIfLoggedIn()
        fetchRemoteConfigValues(
              onSuccess = { WakaLog.d("Remote config values updated") },
              onError = { it.report() }
        )
    }

    fun state(): LiveData<MainState> = state

    private fun checkIfLoggedIn() {
        checkIfUserIsLoggedIn(
              onSuccess = { isLoggedIn ->
                  if (isLoggedIn) {
                      refreshAccessToken.invoke()
                      state.postValue(GoToContent)
                  } else {
                      state.postValue(GoToLogin())
                  }
              },
              onError = { error ->
                  clearCache(onFinish = { state.postValue(GoToLogin()) })
              }
        )
    }

    override fun logIn() {
        state.value = GoToContent
    }

    override fun logOut(forced: Boolean) {
        state.value = GoToLogin(forced)
    }

    override fun onCleared() {
        checkIfUserIsLoggedIn.dispose()
        refreshAccessToken.dispose()
        super.onCleared()
    }

}